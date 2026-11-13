package com.vantara.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import com.vantara.platform.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "notifications.mysql.test", matches = "true")
class NotificationMySqlPersistenceTest {
    private static final Instant NOW = Instant.parse("2026-11-12T14:00:00Z");
    private final RecipientId owner = new RecipientId(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
    private final RecipientId other = new RecipientId(owner.value() + 1);

    @Test
    void validatesSchemaAndPersistsOwnerScopedFiltersAndConcurrentRead() throws Exception {
        int port = Integer.parseInt(requiredEnvironment("NOTIFICATIONS_TEST_MYSQL_PORT"));
        String password = requiredEnvironment("NOTIFICATIONS_TEST_MYSQL_PASSWORD");
        String serverUuid = requiredEnvironment("NOTIFICATIONS_TEST_MYSQL_UUID");
        String url = "jdbc:mysql://127.0.0.1:" + port
                + "/notifications_test?sslMode=DISABLED&allowPublicKeyRetrieval=true&connectionTimeZone=UTC";
        String username = "notifications_test";

        try (var connection = DriverManager.getConnection(url, username, password);
             var statement = connection.createStatement();
             var result = statement.executeQuery("select @@server_uuid")) {
            assertTrue(result.next());
            assertEquals(serverUuid, result.getString(1), "Expected the disposable MySQL instance");
        }

        var registry = new StandardServiceRegistryBuilder()
                .applySetting("jakarta.persistence.jdbc.url", url)
                .applySetting("jakarta.persistence.jdbc.user", username)
                .applySetting("jakarta.persistence.jdbc.password", password)
                .applySetting("hibernate.hbm2ddl.auto", "validate")
                .applySetting("hibernate.physical_naming_strategy",
                        SnakeCaseWithPluralizedTablePhysicalNamingStrategy.class.getName())
                .build();
        try (var factory = new MetadataSources(registry).addAnnotatedClass(NotificationPersistenceEntity.class)
                .buildMetadata().buildSessionFactory()) {
            var entityManager = SharedEntityManagerCreator.createSharedEntityManager(factory);
            var jpa = new JpaRepositoryFactory(entityManager).getRepository(NotificationPersistenceRepository.class);
            var adapter = new NotificationRepositoryImpl(jpa, new DomainEventPublisher(event -> { }));
            var transaction = new TransactionTemplate(new JpaTransactionManager(factory));
            var saved = transaction.execute(status -> {
                adapter.save(create(other));
                return adapter.save(create(owner));
            });

            transaction.executeWithoutResult(status -> {
                assertEquals(List.of(saved.getId()), adapter.findAll(owner, NotificationType.HEALTH_ALERT,
                        NotificationStatus.UNREAD).stream().map(Notification::getId).toList());
                assertTrue(adapter.findAll(owner, NotificationType.VACCINE_REMINDER, null).isEmpty());
                assertTrue(adapter.findAll(owner, null, NotificationStatus.READ).isEmpty());
                assertTrue(adapter.findById(saved.getId(), other).isEmpty());
                assertTrue(adapter.findByIdForUpdate(saved.getId(), other).isEmpty());
            });

            Instant firstRead = NOW.plusSeconds(1);
            var locked = new CountDownLatch(1);
            var releaseLock = new CountDownLatch(1);
            var secondStarted = new CountDownLatch(1);
            try (var executor = Executors.newFixedThreadPool(2)) {
                var first = executor.submit(() -> transaction.execute(status -> {
                    var readAt = markAsRead(adapter, saved, owner, firstRead);
                    locked.countDown();
                    await(releaseLock);
                    return readAt;
                }));
                try {
                    await(locked);
                    var second = executor.submit(() -> transaction.execute(status -> {
                        secondStarted.countDown();
                        return markAsRead(adapter, saved, owner, firstRead.plusSeconds(1));
                    }));
                    await(secondStarted);
                    assertThrows(TimeoutException.class, () -> second.get(300, TimeUnit.MILLISECONDS));
                    releaseLock.countDown();
                    assertEquals(firstRead, first.get(10, TimeUnit.SECONDS));
                    assertEquals(firstRead, second.get(10, TimeUnit.SECONDS));
                } finally {
                    releaseLock.countDown();
                }
            }
            transaction.executeWithoutResult(status -> assertEquals(firstRead,
                    adapter.findById(saved.getId(), owner).orElseThrow().getReadAt()));
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static Instant markAsRead(NotificationRepositoryImpl adapter, Notification saved,
                                      RecipientId recipient, Instant now) {
        var notification = adapter.findByIdForUpdate(saved.getId(), recipient).orElseThrow();
        notification.markAsRead(now);
        return adapter.save(notification).getReadAt();
    }

    private Notification create(RecipientId recipient) {
        return Notification.create(recipient, NotificationType.HEALTH_ALERT,
                new NotificationContent("Title", "Description"), new RelatedEntity("Animal", "A1"), NOW, null);
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable " + name);
        }
        return value;
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out waiting for the other transaction");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError(exception);
        }
    }
}

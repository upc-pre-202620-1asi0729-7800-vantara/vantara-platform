package com.vantara.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.events.NotificationCreatedEvent;
import com.vantara.platform.notifications.domain.model.events.NotificationReadEvent;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationRepositoryImplTest {
    private static final Instant SENT = Instant.parse("2026-11-12T14:00:00Z");
    private static final RecipientId OWNER = new RecipientId(7);

    private final NotificationPersistenceRepository jpa = mock(NotificationPersistenceRepository.class);
    private final List<Object> published = new ArrayList<>();
    private final NotificationRepositoryImpl repository = new NotificationRepositoryImpl(jpa,
            new DomainEventPublisher((ApplicationEventPublisher) published::add));

    @Test
    void roundTripsTheNotificationThroughThePersistenceAssembler() {
        var notification = create();
        when(jpa.findById(notification.getId().value())).thenReturn(Optional.empty());
        when(jpa.save(any(NotificationPersistenceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var saved = repository.save(notification);

        assertEquals(notification.getId(), saved.getId());
        assertEquals(OWNER, saved.getRecipient());
        assertEquals(notification.getType(), saved.getType());
        assertEquals(notification.getContent(), saved.getContent());
        assertEquals(notification.getRelatedEntity(), saved.getRelatedEntity());
        assertEquals(SENT, saved.getSentAt());
        assertEquals(NotificationStatus.UNREAD, saved.getStatus());
    }

    @Test
    void publishesTheRecordedEventsAfterSavingAndOnlyOnce() {
        var notification = create();
        when(jpa.findById(notification.getId().value())).thenReturn(Optional.empty());
        when(jpa.save(any(NotificationPersistenceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        repository.save(notification);
        repository.save(notification);

        assertEquals(List.of(new NotificationCreatedEvent(notification.getId().value(), 7,
                NotificationType.HEALTH_ALERT, SENT)), published);
        assertTrue(notification.domainEvents().isEmpty());
    }

    @Test
    void savingAReadNotificationUpdatesTheStoredEntityAndPublishesTheReadEvent() {
        var notification = create();
        var entity = new NotificationPersistenceEntity();
        entity.setId(notification.getId().value());
        when(jpa.findById(notification.getId().value())).thenReturn(Optional.of(entity));
        when(jpa.save(entity)).thenReturn(entity);
        notification.clearDomainEvents();
        Instant readAt = SENT.plusSeconds(1);
        notification.markAsRead(readAt);

        var saved = repository.save(notification);

        assertEquals(readAt, entity.getReadAt());
        assertEquals(NotificationStatus.READ, saved.getStatus());
        assertEquals(List.of(new NotificationReadEvent(notification.getId().value(), 7, readAt)), published);
    }

    @Test
    void aDifferentRecipientCannotReadOrLockTheNotification() {
        var notification = create();
        var other = new RecipientId(8);
        when(jpa.findByIdAndRecipientUserId(notification.getId().value(), other.value())).thenReturn(Optional.empty());
        when(jpa.findForUpdateByIdAndRecipientUserId(notification.getId().value(), other.value()))
                .thenReturn(Optional.empty());

        assertTrue(repository.findById(notification.getId(), other).isEmpty());
        assertTrue(repository.findByIdForUpdate(notification.getId(), other).isEmpty());

        verify(jpa).findByIdAndRecipientUserId(notification.getId().value(), other.value());
        verify(jpa).findForUpdateByIdAndRecipientUserId(notification.getId().value(), other.value());
        verifyNoMoreInteractions(jpa);
    }

    private Notification create() {
        return Notification.create(OWNER, NotificationType.HEALTH_ALERT,
                new NotificationContent("Title", "Description"), new RelatedEntity("Animal", "A1"), SENT, null);
    }
}

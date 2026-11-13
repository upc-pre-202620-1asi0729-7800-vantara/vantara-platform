package com.vantara.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.assemblers.NotificationPersistenceAssembler;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link NotificationRepository} domain port with Spring Data JPA.
 *
 * <p>After saving, it publishes the domain events recorded by the aggregate.</p>
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationPersistenceRepository persistenceRepository;
    private final DomainEventPublisher eventPublisher;

    public NotificationRepositoryImpl(NotificationPersistenceRepository persistenceRepository,
                                      DomainEventPublisher eventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Notification save(Notification notification) {
        var entity = persistenceRepository.findById(notification.getId().value())
                .orElseGet(NotificationPersistenceEntity::new);
        NotificationPersistenceAssembler.toPersistenceFromDomain(entity, notification);
        var saved = NotificationPersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
        // Events recorded on the original aggregate are published once the state is stored.
        eventPublisher.publishAndClear(notification);
        return saved;
    }

    @Override
    public List<Notification> findAll(RecipientId recipient, NotificationType type, NotificationStatus status) {
        return persistenceRepository.findForRecipient(recipient.value(), type, status == null ? null : status.name())
                .stream().map(NotificationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Optional<Notification> findById(NotificationId id, RecipientId recipient) {
        return persistenceRepository.findByIdAndRecipientUserId(id.value(), recipient.value())
                .map(NotificationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Notification> findByIdForUpdate(NotificationId id, RecipientId recipient) {
        return persistenceRepository.findForUpdateByIdAndRecipientUserId(id.value(), recipient.value())
                .map(NotificationPersistenceAssembler::toDomainFromPersistence);
    }
}

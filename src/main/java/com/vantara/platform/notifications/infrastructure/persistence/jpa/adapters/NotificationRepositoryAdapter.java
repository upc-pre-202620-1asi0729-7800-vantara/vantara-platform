package com.vantara.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.mappers.NotificationPersistenceMapper;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {
    private final NotificationJpaRepository repository;

    public NotificationRepositoryAdapter(NotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification add(Notification notification) {
        return NotificationPersistenceMapper.toDomain(repository.save(NotificationPersistenceMapper.toEntity(notification)));
    }

    @Override
    public List<Notification> findAll(RecipientId recipient, NotificationType type, NotificationStatus status) {
        return repository.findForRecipient(recipient.value(), type, status == null ? null : status.name())
                .stream().map(NotificationPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Notification> findById(UUID id, RecipientId recipient) {
        return repository.findByIdAndRecipientUserId(id, recipient.value()).map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Notification> markAsRead(UUID id, RecipientId recipient, Instant readAt) {
        return repository.lockForReadUpdate(id, recipient.value()).map(entity -> {
            var notification = NotificationPersistenceMapper.toDomain(entity);
            notification.markAsRead(readAt);
            entity.setReadAt(notification.readAt());
            return notification;
        });
    }
}

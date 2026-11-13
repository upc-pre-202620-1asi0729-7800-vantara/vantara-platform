package com.vantara.platform.notifications.domain.repositories;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationId;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;

import java.util.List;
import java.util.Optional;

/**
 * Port that stores {@link Notification} aggregates. Every lookup is scoped to the recipient, so a
 * notification of another user is never found.
 */
public interface NotificationRepository {
    /**
     * Stores the notification and publishes the domain events it recorded.
     */
    Notification save(Notification notification);

    List<Notification> findAll(RecipientId recipient, NotificationType type, NotificationStatus status);

    Optional<Notification> findById(NotificationId id, RecipientId recipient);

    /**
     * Finds the notification holding a write lock until the surrounding transaction ends, so that
     * concurrent requests read it one after another and keep the first stored read time.
     */
    Optional<Notification> findByIdForUpdate(NotificationId id, RecipientId recipient);
}

package com.vantara.platform.notifications.domain.repositories;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification add(Notification notification);

    List<Notification> findAll(RecipientId recipient, NotificationType type, NotificationStatus status);

    Optional<Notification> findById(UUID id, RecipientId recipient);

    /**
     * Records the first read time for a sent notification belonging to the recipient.
     * Concurrent requests must preserve the first stored timestamp.
     */
    Optional<Notification> markAsRead(UUID id, RecipientId recipient, Instant readAt);
}

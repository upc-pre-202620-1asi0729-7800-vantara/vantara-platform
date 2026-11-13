package com.vantara.platform.notifications.domain.model.events;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised when a notification is created ("notification created"), either sent right away or scheduled.
 *
 * @param notificationId identifier of the notification
 * @param recipientId    id of the user who receives it
 * @param type           kind of notification
 * @param occurredAt     instant at which it was created
 */
public record NotificationCreatedEvent(UUID notificationId, long recipientId, NotificationType type,
                                       Instant occurredAt) implements DomainEvent {
}

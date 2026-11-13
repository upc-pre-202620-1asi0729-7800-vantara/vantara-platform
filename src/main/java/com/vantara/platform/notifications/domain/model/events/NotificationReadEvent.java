package com.vantara.platform.notifications.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised the first time the recipient reads a notification ("notification read").
 *
 * @param notificationId identifier of the notification
 * @param recipientId    id of the user who read it
 * @param occurredAt     instant at which it was read
 */
public record NotificationReadEvent(UUID notificationId, long recipientId, Instant occurredAt)
        implements DomainEvent {
}

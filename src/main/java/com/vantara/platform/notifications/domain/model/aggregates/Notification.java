package com.vantara.platform.notifications.domain.model.aggregates;

import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.events.NotificationCreatedEvent;
import com.vantara.platform.notifications.domain.model.events.NotificationReadEvent;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root of a notification addressed to one user. It is created sent or scheduled, and the
 * recipient reads it once it has been sent.
 */
public final class Notification extends AbstractDomainAggregateRoot<Notification> {
    private final NotificationId id;
    private final RecipientId recipient;
    private final NotificationType type;
    private final NotificationContent content;
    private final RelatedEntity relatedEntity;
    private final Instant createdAt;
    private final Instant scheduledAt;
    private Instant sentAt;
    private Instant readAt;

    private Notification(NotificationId id, RecipientId recipient, NotificationType type,
                         NotificationContent content, RelatedEntity relatedEntity, Instant createdAt,
                         Instant scheduledAt, Instant sentAt, Instant readAt) {
        this.id = Objects.requireNonNull(id);
        this.recipient = Objects.requireNonNull(recipient);
        this.type = Objects.requireNonNull(type);
        this.content = Objects.requireNonNull(content);
        this.relatedEntity = relatedEntity;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.readAt = readAt;

        if ((scheduledAt != null && scheduledAt.isBefore(createdAt))
                || (sentAt == null && scheduledAt == null)
                || (sentAt != null && sentAt.isBefore(scheduledAt == null ? createdAt : scheduledAt))
                || (readAt != null && (sentAt == null || readAt.isBefore(sentAt)))) {
            throw new IllegalArgumentException("Invalid notification timestamps");
        }
    }

    /**
     * Factory method that creates a notification ("create notification").
     *
     * @param scheduledAt instant to send it at, or {@code null} to send it right away
     * @return the new notification with a {@link NotificationCreatedEvent} recorded
     */
    public static Notification create(RecipientId recipient, NotificationType type, NotificationContent content,
                                      RelatedEntity relatedEntity, Instant now, Instant scheduledAt) {
        Instant sentAt = scheduledAt == null || scheduledAt.equals(now) ? now : null;
        var notification = new Notification(NotificationId.generate(), recipient, type, content, relatedEntity,
                now, scheduledAt, sentAt, null);
        notification.registerDomainEvent(new NotificationCreatedEvent(notification.id.value(),
                recipient.value(), type, now));
        return notification;
    }

    /**
     * Rebuilds a notification from its persisted state. It does not record any domain event.
     */
    public static Notification reconstitute(NotificationId id, RecipientId recipient, NotificationType type,
                                            NotificationContent content, RelatedEntity relatedEntity,
                                            Instant createdAt, Instant scheduledAt, Instant sentAt, Instant readAt) {
        return new Notification(id, recipient, type, content, relatedEntity, createdAt, scheduledAt, sentAt, readAt);
    }

    public void send(Instant now) {
        Objects.requireNonNull(now);
        if (sentAt == null) {
            if (now.isBefore(scheduledAt)) {
                throw new IllegalArgumentException("Notification is scheduled for a later time");
            }
            sentAt = now;
        }
    }

    /**
     * Records the first read time. Reading it again keeps the first time and records nothing.
     *
     * @throws NotificationNotSentException when the notification is still scheduled
     */
    public void markAsRead(Instant now) {
        Objects.requireNonNull(now);
        if (readAt != null) {
            return;
        }
        if (sentAt == null) {
            throw new NotificationNotSentException();
        }
        if (now.isBefore(sentAt)) {
            throw new IllegalArgumentException("Read time cannot precede sent time");
        }
        readAt = now;
        registerDomainEvent(new NotificationReadEvent(id.value(), recipient.value(), now));
    }

    public NotificationStatus getStatus() {
        if (readAt != null) {
            return NotificationStatus.READ;
        }
        return sentAt == null ? NotificationStatus.SCHEDULED : NotificationStatus.UNREAD;
    }

    public NotificationId getId() { return id; }
    public RecipientId getRecipient() { return recipient; }
    public NotificationType getType() { return type; }
    public NotificationContent getContent() { return content; }
    public RelatedEntity getRelatedEntity() { return relatedEntity; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getReadAt() { return readAt; }
}

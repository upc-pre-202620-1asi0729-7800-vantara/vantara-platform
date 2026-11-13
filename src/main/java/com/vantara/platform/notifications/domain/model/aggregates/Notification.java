package com.vantara.platform.notifications.domain.model.aggregates;

import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Notification extends AbstractDomainAggregateRoot<Notification> {
    private final UUID id;
    private final RecipientId recipient;
    private final NotificationType type;
    private final NotificationContent content;
    private final RelatedEntity relatedEntity;
    private final Instant createdAt;
    private final Instant scheduledAt;
    private Instant sentAt;
    private Instant readAt;

    private Notification(UUID id, RecipientId recipient, NotificationType type, NotificationContent content,
                         RelatedEntity relatedEntity, Instant createdAt, Instant scheduledAt,
                         Instant sentAt, Instant readAt) {
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

    public static Notification create(RecipientId recipient, NotificationType type, NotificationContent content,
                                      RelatedEntity relatedEntity, Instant now, Instant scheduledAt) {
        Instant sentAt = scheduledAt == null || scheduledAt.equals(now) ? now : null;
        return new Notification(UUID.randomUUID(), recipient, type, content, relatedEntity, now,
                scheduledAt, sentAt, null);
    }

    public static Notification reconstitute(UUID id, RecipientId recipient, NotificationType type,
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
    }

    public NotificationStatus status() {
        if (readAt != null) {
            return NotificationStatus.READ;
        }
        return sentAt == null ? NotificationStatus.SCHEDULED : NotificationStatus.UNREAD;
    }

    public UUID id() { return id; }
    public RecipientId recipient() { return recipient; }
    public NotificationType type() { return type; }
    public NotificationContent content() { return content; }
    public RelatedEntity relatedEntity() { return relatedEntity; }
    public Instant createdAt() { return createdAt; }
    public Instant scheduledAt() { return scheduledAt; }
    public Instant sentAt() { return sentAt; }
    public Instant readAt() { return readAt; }
}

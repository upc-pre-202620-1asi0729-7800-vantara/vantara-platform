package com.vantara.platform.notifications.infrastructure.persistence.jpa.entities;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationJpaEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false)
    private long recipientUserId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 64)
    private String relatedEntityType;

    @Column(nullable = false, length = 128)
    private String relatedEntityId;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant scheduledAt;
    private Instant sentAt;
    private Instant readAt;

    protected NotificationJpaEntity() {
    }

    public NotificationJpaEntity(UUID id, long recipientUserId, NotificationType type, String title,
                                 String description, String relatedEntityType, String relatedEntityId,
                                 Instant createdAt, Instant scheduledAt, Instant sentAt, Instant readAt) {
        this.id = id;
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = createdAt;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
    }

    public UUID getId() { return id; }
    public long getRecipientUserId() { return recipientUserId; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public String getRelatedEntityId() { return relatedEntityId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}

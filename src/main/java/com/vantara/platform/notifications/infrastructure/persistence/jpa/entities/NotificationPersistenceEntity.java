package com.vantara.platform.notifications.infrastructure.persistence.jpa.entities;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA persistence entity of a notification. It holds no business logic. The primary key is the public
 * UUID of the notification, so it does not extend the Long-keyed auditable base entity.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class NotificationPersistenceEntity {
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
}

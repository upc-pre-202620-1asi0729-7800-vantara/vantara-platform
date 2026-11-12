package com.vantara.platform.notifications.interfaces.rest.resources;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import java.time.Instant;

public record NotificationResource(String id, String recipientUserId, NotificationType type,
                                   String title, String description, String channel, String status,
                                   Instant scheduledAt, Instant sentAt, Instant readAt,
                                   String relatedEntityType, String relatedEntityId, Instant createdAt) {
}

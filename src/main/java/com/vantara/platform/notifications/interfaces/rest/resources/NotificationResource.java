package com.vantara.platform.notifications.interfaces.rest.resources;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "NotificationResource", description = "A notification of the authenticated user")
public record NotificationResource(
        @Schema(example = "3f2b8c1e-6a4d-4c0e-9d57-2a1f0b7e5c11") String id,
        @Schema(description = "Id of the user who receives the notification", example = "7") String recipientUserId,
        @Schema(example = "HEALTH_ALERT") NotificationType type,
        @Schema(example = "Animal requires follow-up") String title,
        @Schema(example = "Animal #018 has a health alert and requires follow-up.") String description,
        @Schema(description = "Delivery channel; only in-app notifications exist", example = "in_app") String channel,
        @Schema(description = "scheduled, unread or read", example = "unread") String status,
        @Schema(description = "Instant it is due, when it was scheduled") Instant scheduledAt,
        @Schema(description = "Instant it was sent; empty while scheduled") Instant sentAt,
        @Schema(description = "Instant it was first read; empty while unread") Instant readAt,
        @Schema(example = "Animal") String relatedEntityType,
        @Schema(example = "18") String relatedEntityId,
        Instant createdAt) {
}

package com.vantara.platform.notifications.interfaces.rest.resources;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationResource(
        @NotNull NotificationType type,
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Size(max = 64) String relatedEntityType,
        @NotBlank @Size(max = 128) String relatedEntityId) {
}

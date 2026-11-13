package com.vantara.platform.notifications.interfaces.rest.resources;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateNotificationResource",
        description = "Data to notify the authenticated user. The recipient is never taken from the body.")
public record CreateNotificationResource(
        @Schema(example = "HEALTH_ALERT") @NotNull NotificationType type,
        @Schema(example = "Animal requires follow-up") @NotBlank @Size(max = 160) String title,
        @Schema(example = "Animal #018 has a health alert and requires follow-up.")
        @NotBlank @Size(max = 2000) String description,
        @Schema(example = "Animal") @NotBlank @Size(max = 64) String relatedEntityType,
        @Schema(example = "18") @NotBlank @Size(max = 128) String relatedEntityId) {
}

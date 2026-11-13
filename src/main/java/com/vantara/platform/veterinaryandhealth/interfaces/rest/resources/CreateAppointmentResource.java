package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request resource used to schedule a veterinary appointment.
 */
public record CreateAppointmentResource(
        @NotNull Long rancherId,
        @NotNull Long veterinarianId,
        @NotNull Long requestedBy,
        @NotBlank String origin,
        @NotNull LocalDateTime scheduledAt,
        @NotBlank @Size(max = 255) String reason) {
}

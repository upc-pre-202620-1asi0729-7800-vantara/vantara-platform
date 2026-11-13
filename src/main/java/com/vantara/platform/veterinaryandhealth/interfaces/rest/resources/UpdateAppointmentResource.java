package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Partial update of a veterinary appointment. Fields that are omitted are left unchanged.
 */
public record UpdateAppointmentResource(
        String status,
        @Size(max = 255) String cancellationReason,
        LocalDateTime scheduledAt,
        @Size(max = 255) String reason) {
}

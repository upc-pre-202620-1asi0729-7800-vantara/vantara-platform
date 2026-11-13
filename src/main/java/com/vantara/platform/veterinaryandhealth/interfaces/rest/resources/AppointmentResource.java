package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Appointment returned by the API.
 */
public record AppointmentResource(
        String id,
        Long rancherId,
        Long veterinarianId,
        Long requestedBy,
        String origin,
        LocalDateTime scheduledAt,
        String reason,
        String status,
        String cancellationReason,
        LocalDate createdAt) {
}

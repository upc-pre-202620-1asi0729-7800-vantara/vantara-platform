
package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;

/**
 * Command to update a veterinary appointment.
 *
 * @param appointmentId      identifier of the appointment
 * @param actorId            authenticated user performing the operation
 * @param status             new status, or null
 * @param cancellationReason reason required when cancelling
 * @param scheduledAt        new date and time, or null
 * @param reason             new reason, or null
 */
public record UpdateAppointmentCommand(
        String appointmentId,
        Long actorId,
        AppointmentStatus status,
        String cancellationReason,
        LocalDateTime scheduledAt,
        String reason
) {

    public UpdateAppointmentCommand {
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }

        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("Actor id must be valid");
        }
    }
}

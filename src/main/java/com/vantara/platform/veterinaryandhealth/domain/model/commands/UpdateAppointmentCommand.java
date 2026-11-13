package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;

/**
 * Command to update a veterinary appointment. Fields that are null are left unchanged.
 *
 * @param appointmentId      identifier of the appointment to update
 * @param status             new status, or null
 * @param cancellationReason reason required when the status becomes cancelled
 * @param scheduledAt        new date and time, or null
 * @param reason             new reason of the appointment, or null
 */
public record UpdateAppointmentCommand(
        String appointmentId,
        AppointmentStatus status,
        String cancellationReason,
        LocalDateTime scheduledAt,
        String reason) {

    public UpdateAppointmentCommand {
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }
    }
}

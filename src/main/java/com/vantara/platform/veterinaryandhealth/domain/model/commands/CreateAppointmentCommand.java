package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentOrigin;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Command to schedule a veterinary appointment.
 *
 * @param rancherId      rancher attended
 * @param veterinarianId assigned veterinarian
 * @param requestedBy    user who requested the appointment
 * @param origin         origin of the appointment
 * @param scheduledAt    date and time of the appointment
 * @param reason         reason of the appointment
 */
public record CreateAppointmentCommand(
        UserId rancherId,
        UserId veterinarianId,
        UserId requestedBy,
        AppointmentOrigin origin,
        LocalDateTime scheduledAt,
        String reason) {

    public CreateAppointmentCommand {
        Objects.requireNonNull(rancherId, "Rancher id cannot be null");
        Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        Objects.requireNonNull(requestedBy, "Requester id cannot be null");
        Objects.requireNonNull(origin, "Origin cannot be null");
        Objects.requireNonNull(scheduledAt, "Scheduled date cannot be null");
    }
}

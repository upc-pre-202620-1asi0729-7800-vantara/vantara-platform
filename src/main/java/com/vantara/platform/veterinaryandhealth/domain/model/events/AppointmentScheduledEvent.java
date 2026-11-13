package com.vantara.platform.veterinaryandhealth.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Raised when a veterinary appointment is scheduled. It carries primitive values so other contexts can consume it.
 *
 * @param appointmentId   identifier of the appointment
 * @param rancherId       identifier of the rancher attended
 * @param veterinarianId  identifier of the assigned veterinarian
 * @param scheduledAt     date and time of the appointment
 * @param occurredAt      instant at which the appointment was scheduled
 */
public record AppointmentScheduledEvent(
        String appointmentId,
        Long rancherId,
        Long veterinarianId,
        LocalDateTime scheduledAt,
        Instant occurredAt) implements DomainEvent {
}

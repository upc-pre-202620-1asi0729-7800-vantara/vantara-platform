package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.vantara.platform.veterinaryandhealth.domain.model.events.AppointmentScheduledEvent;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentOrigin;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Aggregate root of a veterinary appointment between a rancher and a veterinarian.
 * Animals are linked to the appointment through {@link AppointmentAnimal}, which references it by id.
 */
public class Appointment extends AbstractDomainAggregateRoot<Appointment> {

    private static final int REASON_MAX_LENGTH = 255;

    private final String id;
    private final UserId rancherId;
    private final UserId veterinarianId;
    private final UserId requestedBy;
    private final AppointmentOrigin origin;
    private LocalDateTime scheduledAt;
    private String reason;
    private AppointmentStatus status;
    private String cancellationReason;
    private final LocalDate createdAt;

    private Appointment(String id, UserId rancherId, UserId veterinarianId, UserId requestedBy,
                        AppointmentOrigin origin, LocalDateTime scheduledAt, String reason,
                        AppointmentStatus status, String cancellationReason, LocalDate createdAt) {
        this.id = Objects.requireNonNull(id, "Appointment id cannot be null");
        this.rancherId = Objects.requireNonNull(rancherId, "Rancher id cannot be null");
        this.veterinarianId = Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        this.requestedBy = Objects.requireNonNull(requestedBy, "Requester id cannot be null");
        this.origin = Objects.requireNonNull(origin, "Origin cannot be null");
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "Scheduled date cannot be null");
        this.reason = requireReason(reason, "Reason");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.cancellationReason = cancellationReason;
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date cannot be null");
    }

    /**
     * Schedules a new appointment and records an {@link AppointmentScheduledEvent}.
     */
    public static Appointment schedule(String id, UserId rancherId, UserId veterinarianId, UserId requestedBy,
                                       AppointmentOrigin origin, LocalDateTime scheduledAt, String reason,
                                       Instant now) {
        Objects.requireNonNull(now, "Creation instant cannot be null");
        var appointment = new Appointment(id, rancherId, veterinarianId, requestedBy, origin, scheduledAt,
                reason, AppointmentStatus.scheduled, null, now.atZone(ZoneOffset.UTC).toLocalDate());
        appointment.registerDomainEvent(new AppointmentScheduledEvent(
                id, rancherId.value(), veterinarianId.value(), scheduledAt, now));
        return appointment;
    }

    /**
     * Rebuilds an appointment from its persisted state. It does not record any domain event.
     */
    public static Appointment reconstitute(String id, UserId rancherId, UserId veterinarianId, UserId requestedBy,
                                           AppointmentOrigin origin, LocalDateTime scheduledAt, String reason,
                                           AppointmentStatus status, String cancellationReason, LocalDate createdAt) {
        return new Appointment(id, rancherId, veterinarianId, requestedBy, origin, scheduledAt, reason,
                status, cancellationReason, createdAt);
    }

    public boolean isScheduled() {
        return status == AppointmentStatus.scheduled;
    }

    /**
     * @return true when the appointment already has the target status or is still scheduled
     */
    public boolean canChangeTo(AppointmentStatus target) {
        return status == target || isScheduled();
    }

    public void reschedule(LocalDateTime newScheduledAt) {
        requireScheduled();
        this.scheduledAt = Objects.requireNonNull(newScheduledAt, "Scheduled date cannot be null");
    }

    public void changeReason(String newReason) {
        requireScheduled();
        this.reason = requireReason(newReason, "Reason");
    }

    /**
     * Completes or cancels a scheduled appointment. Cancelling requires a cancellation reason.
     */
    public void changeStatus(AppointmentStatus target, String newCancellationReason) {
        Objects.requireNonNull(target, "Target status cannot be null");
        if (status == target) {
            return;
        }
        requireScheduled();
        switch (target) {
            case completed -> status = AppointmentStatus.completed;
            case cancelled -> {
                this.cancellationReason = requireReason(newCancellationReason, "Cancellation reason");
                status = AppointmentStatus.cancelled;
            }
            case scheduled -> throw new IllegalStateException("An appointment cannot return to scheduled");
        }
    }

    private void requireScheduled() {
        if (!isScheduled()) {
            throw new IllegalStateException("Only scheduled appointments can be modified");
        }
    }

    private static String requireReason(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        var trimmed = value.trim();
        if (trimmed.length() > REASON_MAX_LENGTH) {
            throw new IllegalArgumentException(label + " cannot exceed 255 characters");
        }
        return trimmed;
    }

    public String getId() {
        return id;
    }

    public UserId getRancherId() {
        return rancherId;
    }

    public UserId getVeterinarianId() {
        return veterinarianId;
    }

    public UserId getRequestedBy() {
        return requestedBy;
    }

    public AppointmentOrigin getOrigin() {
        return origin;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getReason() {
        return reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}

package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.AppointmentPersistenceEntity;

/**
 * Translates between the {@link Appointment} aggregate and its persistence entity.
 */
public final class AppointmentPersistenceAssembler {

    private AppointmentPersistenceAssembler() {
    }

    public static Appointment toDomainFromPersistence(AppointmentPersistenceEntity entity) {
        return Appointment.reconstitute(
                entity.getId(),
                new UserId(entity.getRancherId()),
                new UserId(entity.getVeterinarianId()),
                new UserId(entity.getRequestedBy()),
                entity.getOrigin(),
                entity.getScheduledAt(),
                entity.getReason(),
                entity.getStatus(),
                entity.getCancellationReason(),
                entity.getCreatedAt());
    }

    public static AppointmentPersistenceEntity toPersistenceFromDomain(Appointment appointment) {
        var entity = new AppointmentPersistenceEntity();
        entity.setId(appointment.getId());
        entity.setRancherId(appointment.getRancherId().value());
        entity.setVeterinarianId(appointment.getVeterinarianId().value());
        entity.setRequestedBy(appointment.getRequestedBy().value());
        entity.setOrigin(appointment.getOrigin());
        entity.setScheduledAt(appointment.getScheduledAt());
        entity.setReason(appointment.getReason());
        entity.setStatus(appointment.getStatus());
        entity.setCancellationReason(appointment.getCancellationReason());
        entity.setCreatedAt(appointment.getCreatedAt());
        return entity;
    }
}

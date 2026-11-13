package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.AppointmentAnimalPersistenceEntity;

/**
 * Translates between the {@link AppointmentAnimal} aggregate and its persistence entity.
 */
public final class AppointmentAnimalPersistenceAssembler {

    private AppointmentAnimalPersistenceAssembler() {
    }

    public static AppointmentAnimal toDomainFromPersistence(AppointmentAnimalPersistenceEntity entity) {
        return AppointmentAnimal.reconstitute(
                entity.getId(),
                entity.getAppointmentId(),
                new AnimalId(entity.getAnimalId()));
    }

    public static AppointmentAnimalPersistenceEntity toPersistenceFromDomain(AppointmentAnimal appointmentAnimal) {
        var entity = new AppointmentAnimalPersistenceEntity();
        entity.setId(appointmentAnimal.getId());
        entity.setAppointmentId(appointmentAnimal.getAppointmentId());
        entity.setAnimalId(appointmentAnimal.getAnimalId().value());
        return entity;
    }
}

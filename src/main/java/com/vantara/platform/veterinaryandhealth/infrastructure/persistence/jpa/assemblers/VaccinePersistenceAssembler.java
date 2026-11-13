package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.VaccinePersistenceEntity;

/**
 * Translates between the {@link Vaccine} aggregate and its persistence entity.
 */
public final class VaccinePersistenceAssembler {

    private VaccinePersistenceAssembler() {
    }

    public static Vaccine toDomainFromPersistence(VaccinePersistenceEntity entity) {
        return Vaccine.reconstitute(
                entity.getId(),
                new AnimalId(entity.getAnimalId()),
                new UserId(entity.getVeterinarianId()),
                entity.getVaccineType(),
                entity.getName(),
                new Dose(entity.getDoseAmount(), entity.getDoseUnit()),
                entity.getLaboratory(),
                entity.getLotNumber(),
                entity.getAppliedAt(),
                entity.getNextDoseAt(),
                entity.getRoute(),
                entity.getApplicationSite(),
                entity.getStatus(),
                entity.getObservations());
    }

    public static VaccinePersistenceEntity toPersistenceFromDomain(Vaccine vaccine) {
        var entity = new VaccinePersistenceEntity();
        entity.setId(vaccine.getId());
        entity.setAnimalId(vaccine.getAnimalId().value());
        entity.setVeterinarianId(vaccine.getVeterinarianId().value());
        entity.setVaccineType(vaccine.getVaccineType());
        entity.setName(vaccine.getName());
        entity.setDoseAmount(vaccine.getDose().amount());
        entity.setDoseUnit(vaccine.getDose().unit());
        entity.setLaboratory(vaccine.getLaboratory());
        entity.setLotNumber(vaccine.getLotNumber());
        entity.setAppliedAt(vaccine.getAppliedAt());
        entity.setNextDoseAt(vaccine.getNextDoseAt());
        entity.setRoute(vaccine.getRoute());
        entity.setApplicationSite(vaccine.getApplicationSite());
        entity.setStatus(vaccine.getStatus());
        entity.setObservations(vaccine.getObservations());
        return entity;
    }
}

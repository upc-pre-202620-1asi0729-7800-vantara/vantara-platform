package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.TreatmentPersistenceEntity;

/**
 * Translates between the {@link Treatment} aggregate and its persistence entity.
 */
public final class TreatmentPersistenceAssembler {

    private TreatmentPersistenceAssembler() {
    }

    public static Treatment toDomainFromPersistence(TreatmentPersistenceEntity entity) {
        return Treatment.reconstitute(
                entity.getId(),
                entity.getMedicalRecordId(),
                new AnimalId(entity.getAnimalId()),
                entity.getMedicationId(),
                entity.getTreatmentType(),
                new Dose(entity.getDoseAmount(), entity.getDoseUnit()),
                entity.getRoute(),
                entity.getFrequencyHours(),
                entity.getDurationDays(),
                entity.getScheduledAt(),
                entity.getAdministeredAt(),
                entity.getNextDate(),
                entity.getStatus(),
                entity.getInstructions());
    }

    public static TreatmentPersistenceEntity toPersistenceFromDomain(Treatment treatment) {
        var entity = new TreatmentPersistenceEntity();
        entity.setId(treatment.getId());
        entity.setMedicalRecordId(treatment.getMedicalRecordId());
        entity.setAnimalId(treatment.getAnimalId().value());
        entity.setMedicationId(treatment.getMedicationId());
        entity.setTreatmentType(treatment.getTreatmentType());
        entity.setDoseAmount(treatment.getDose().amount());
        entity.setDoseUnit(treatment.getDose().unit());
        entity.setRoute(treatment.getRoute());
        entity.setFrequencyHours(treatment.getFrequencyHours());
        entity.setDurationDays(treatment.getDurationDays());
        entity.setScheduledAt(treatment.getScheduledAt());
        entity.setAdministeredAt(treatment.getAdministeredAt());
        entity.setNextDate(treatment.getNextDate());
        entity.setStatus(treatment.getStatus());
        entity.setInstructions(treatment.getInstructions());
        return entity;
    }
}

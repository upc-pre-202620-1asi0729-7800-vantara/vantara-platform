package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.MedicalRecordPersistenceEntity;

/**
 * Translates between the {@link MedicalRecord} aggregate and its persistence entity.
 */
public final class MedicalRecordPersistenceAssembler {

    private MedicalRecordPersistenceAssembler() {
    }

    public static MedicalRecord toDomainFromPersistence(MedicalRecordPersistenceEntity entity) {
        return MedicalRecord.reconstitute(
                entity.getId(),
                entity.getAppointmentId(),
                new AnimalId(entity.getAnimalId()),
                new UserId(entity.getVeterinarianId()),
                entity.getRecordedAt(),
                entity.getSymptoms(),
                entity.getDiagnosis(),
                entity.getSeverity(),
                entity.getHealthStatus(),
                entity.getNotes());
    }

    public static MedicalRecordPersistenceEntity toPersistenceFromDomain(MedicalRecord medicalRecord) {
        var entity = new MedicalRecordPersistenceEntity();
        entity.setId(medicalRecord.getId());
        entity.setAppointmentId(medicalRecord.getAppointmentId());
        entity.setAnimalId(medicalRecord.getAnimalId().value());
        entity.setVeterinarianId(medicalRecord.getVeterinarianId().value());
        entity.setRecordedAt(medicalRecord.getRecordedAt());
        entity.setSymptoms(medicalRecord.getSymptoms());
        entity.setDiagnosis(medicalRecord.getDiagnosis());
        entity.setSeverity(medicalRecord.getSeverity());
        entity.setHealthStatus(medicalRecord.getHealthStatus());
        entity.setNotes(medicalRecord.getNotes());
        return entity;
    }
}

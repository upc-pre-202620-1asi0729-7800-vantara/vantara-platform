package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.MedicalRecordResource;

/**
 * Translates the {@link MedicalRecord} aggregate into its HTTP resource.
 */
public final class MedicalRecordResourceFromEntityAssembler {

    private MedicalRecordResourceFromEntityAssembler() {
    }

    public static MedicalRecordResource toResourceFromEntity(MedicalRecord medicalRecord) {
        return new MedicalRecordResource(
                medicalRecord.getId(),
                medicalRecord.getAppointmentId(),
                medicalRecord.getAnimalId().value(),
                medicalRecord.getVeterinarianId().value(),
                medicalRecord.getRecordedAt(),
                medicalRecord.getSymptoms(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getSeverity().name(),
                medicalRecord.getHealthStatus().name(),
                medicalRecord.getNotes());
    }
}

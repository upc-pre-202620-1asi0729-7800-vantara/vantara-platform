package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalHealthStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.MedicalRecordSeverity;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Command to register a clinical record for an animal.
 *
 * @param appointmentId  appointment that motivated the record, or null
 * @param animalId       animal diagnosed
 * @param veterinarianId veterinarian who issues the diagnosis
 * @param recordedAt     date and time of the diagnosis
 * @param symptoms       symptoms identified
 * @param diagnosis      diagnosis issued
 * @param severity       severity of the diagnosis
 * @param healthStatus   resulting health status of the animal
 * @param notes          additional notes, or null
 */
public record RegisterMedicalRecordCommand(
        String appointmentId,
        AnimalId animalId,
        UserId veterinarianId,
        LocalDateTime recordedAt,
        String symptoms,
        String diagnosis,
        MedicalRecordSeverity severity,
        AnimalHealthStatus healthStatus,
        String notes) {

    public RegisterMedicalRecordCommand {
        if (appointmentId != null && appointmentId.isBlank()) {
            appointmentId = null;
        }
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        Objects.requireNonNull(recordedAt, "Recorded date cannot be null");
        Objects.requireNonNull(severity, "Severity cannot be null");
        Objects.requireNonNull(healthStatus, "Health status cannot be null");
    }
}

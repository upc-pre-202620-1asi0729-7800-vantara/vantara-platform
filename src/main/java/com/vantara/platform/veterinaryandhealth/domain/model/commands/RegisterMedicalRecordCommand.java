
package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalHealthStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.MedicalRecordSeverity;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public record RegisterMedicalRecordCommand(
        String appointmentId,
        AnimalId animalId,
        UserId veterinarianId,
        Long actorId,
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

        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("Actor id must be valid");
        }
    }
}

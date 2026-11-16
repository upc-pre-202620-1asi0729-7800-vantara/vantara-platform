
package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public record PrescribeTreatmentCommand(
        Long actorId,
        String medicalRecordId,
        AnimalId animalId,
        String medicationId,
        String treatmentType,
        Dose dose,
        String route,
        int frequencyHours,
        int durationDays,
        LocalDateTime scheduledAt,
        LocalDateTime administeredAt,
        LocalDate nextDate,
        String instructions) {

    public PrescribeTreatmentCommand {
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("Actor id must be valid");
        }

        Objects.requireNonNull(medicalRecordId, "Medical record id cannot be null");
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(dose, "Dose cannot be null");
        Objects.requireNonNull(scheduledAt, "Scheduled date cannot be null");
    }
}

package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Command to prescribe a treatment within a clinical record.
 *
 * @param medicalRecordId  clinical record that motivates the treatment
 * @param animalId         animal being treated
 * @param medicationId     medication prescribed
 * @param treatmentType    type of treatment, for example preventive or curative
 * @param dose             quantity and unit administered per dose
 * @param route            administration route
 * @param frequencyHours   hours between doses
 * @param durationDays     total duration in days
 * @param scheduledAt      scheduled start of the treatment
 * @param administeredAt   date of the first administration, or null
 * @param nextDate         date of the next dose, or null
 * @param instructions     additional instructions, or null
 */
public record PrescribeTreatmentCommand(
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
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(dose, "Dose cannot be null");
        Objects.requireNonNull(scheduledAt, "Scheduled date cannot be null");
    }
}

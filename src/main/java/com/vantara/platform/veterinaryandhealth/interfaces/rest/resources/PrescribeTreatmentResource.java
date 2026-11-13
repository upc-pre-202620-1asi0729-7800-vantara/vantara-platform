package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Request resource used to prescribe a treatment. Instants are received in UTC.
 */
public record PrescribeTreatmentResource(
        @NotBlank String medicalRecordId,
        @NotBlank String animalId,
        @NotBlank String medicationId,
        @NotBlank String treatmentType,
        @NotNull @Positive BigDecimal dose,
        @NotBlank String doseUnit,
        @NotBlank String route,
        @Positive int frequencyHours,
        @Positive int durationDays,
        @NotNull Instant scheduledAt,
        Instant administeredAt,
        LocalDate nextDate,
        String instructions) {
}

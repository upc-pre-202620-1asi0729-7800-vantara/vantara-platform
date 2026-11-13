package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request resource used to register a vaccine dose.
 */
public record RegisterVaccineResource(
        @NotBlank String animalId,
        @NotNull Long veterinarianId,
        String vaccineType,
        @NotBlank @Size(max = 120) String name,
        @NotNull @Positive BigDecimal dose,
        @NotBlank String doseUnit,
        String laboratory,
        String lotNumber,
        @NotNull LocalDate appliedAt,
        LocalDate nextDoseAt,
        String route,
        String applicationSite,
        @NotBlank String status,
        String observations) {
}

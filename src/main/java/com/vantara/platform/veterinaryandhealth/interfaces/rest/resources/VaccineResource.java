package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vaccine dose returned by the API.
 */
public record VaccineResource(
        String id,
        String animalId,
        Long veterinarianId,
        String vaccineType,
        String name,
        BigDecimal dose,
        String doseUnit,
        String laboratory,
        String lotNumber,
        LocalDate appliedAt,
        LocalDate nextDoseAt,
        String route,
        String applicationSite,
        String status,
        String observations) {
}

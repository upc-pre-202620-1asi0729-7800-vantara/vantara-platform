package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Treatment returned by the API.
 */
public record TreatmentResource(
        String id,
        String medicalRecordId,
        String animalId,
        String medicationId,
        String treatmentType,
        BigDecimal dose,
        String doseUnit,
        String route,
        int frequencyHours,
        int durationDays,
        LocalDateTime scheduledAt,
        LocalDateTime administeredAt,
        LocalDate nextDate,
        String status,
        String instructions) {
}

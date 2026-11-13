package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Clinical record returned by the API.
 */
public record MedicalRecordResource(
        String id,
        String appointmentId,
        String animalId,
        Long veterinarianId,
        LocalDateTime recordedAt,
        String symptoms,
        String diagnosis,
        String severity,
        String healthStatus,
        String notes) {
}

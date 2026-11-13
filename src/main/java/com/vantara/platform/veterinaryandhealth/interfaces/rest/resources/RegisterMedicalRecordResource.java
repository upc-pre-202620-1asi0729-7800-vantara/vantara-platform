package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request resource used to register a clinical record.
 */
public record RegisterMedicalRecordResource(
        String appointmentId,
        @NotBlank String animalId,
        @NotNull Long veterinarianId,
        @NotNull LocalDateTime recordedAt,
        @NotBlank @Size(max = 1000) String symptoms,
        @NotBlank @Size(max = 1000) String diagnosis,
        @NotBlank String severity,
        @NotBlank String healthStatus,
        @Size(max = 1000) String notes) {
}

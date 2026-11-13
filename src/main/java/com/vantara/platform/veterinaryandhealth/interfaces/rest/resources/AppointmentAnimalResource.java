package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Link between an appointment and an animal. Used as response and as request body.
 */
public record AppointmentAnimalResource(
        String id,
        @NotBlank String appointmentId,
        @NotBlank String animalId) {
}

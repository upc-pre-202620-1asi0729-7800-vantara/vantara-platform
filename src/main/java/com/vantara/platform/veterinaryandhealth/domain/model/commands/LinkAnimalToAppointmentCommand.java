
package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.Objects;

public record LinkAnimalToAppointmentCommand(
        String appointmentId,
        AnimalId animalId,
        Long actorId
) {

    public LinkAnimalToAppointmentCommand {
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }

        Objects.requireNonNull(animalId, "Animal id cannot be null");

        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("Actor id must be valid");
        }
    }
}

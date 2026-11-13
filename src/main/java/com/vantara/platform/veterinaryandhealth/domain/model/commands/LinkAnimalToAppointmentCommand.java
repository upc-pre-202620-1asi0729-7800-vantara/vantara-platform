package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.Objects;

/**
 * Command to attach an animal to a veterinary appointment.
 *
 * @param appointmentId identifier of the appointment
 * @param animalId      identifier of the animal to attach
 */
public record LinkAnimalToAppointmentCommand(String appointmentId, AnimalId animalId) {

    public LinkAnimalToAppointmentCommand {
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }
        Objects.requireNonNull(animalId, "Animal id cannot be null");
    }
}

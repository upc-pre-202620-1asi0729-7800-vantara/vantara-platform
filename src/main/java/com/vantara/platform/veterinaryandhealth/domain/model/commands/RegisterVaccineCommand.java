
package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.VaccineStatus;

import java.time.LocalDate;
import java.util.Objects;

public record RegisterVaccineCommand(
        Long actorId,
        String appointmentId,
        AnimalId animalId,
        UserId veterinarianId,
        String vaccineType,
        String name,
        Dose dose,
        String laboratory,
        String lotNumber,
        LocalDate appliedAt,
        LocalDate nextDoseAt,
        String route,
        String applicationSite,
        VaccineStatus status,
        String observations
) {

    public RegisterVaccineCommand {
        if (actorId == null || actorId <= 0) {
            throw new IllegalArgumentException("Actor id must be valid");
        }

        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }

        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        Objects.requireNonNull(dose, "Dose cannot be null");
        Objects.requireNonNull(appliedAt, "Application date cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }
}

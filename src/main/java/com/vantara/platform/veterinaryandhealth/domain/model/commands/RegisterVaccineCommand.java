package com.vantara.platform.veterinaryandhealth.domain.model.commands;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.VaccineStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Command to register a vaccine dose applied to an animal.
 *
 * @param animalId        animal vaccinated
 * @param veterinarianId  veterinarian who applied the dose
 * @param vaccineType     type of vaccine, or null
 * @param name            commercial name of the vaccine
 * @param dose            quantity and unit applied
 * @param laboratory      laboratory that produced the vaccine, or null
 * @param lotNumber       batch number of the vaccine, or null
 * @param appliedAt       date of application
 * @param nextDoseAt      date of the next dose, or null
 * @param route           application route, or null
 * @param applicationSite body site of the application, or null
 * @param status          state of the dose
 * @param observations    additional observations, or null
 */
public record RegisterVaccineCommand(
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
        String observations) {

    public RegisterVaccineCommand {
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        Objects.requireNonNull(dose, "Dose cannot be null");
        Objects.requireNonNull(appliedAt, "Application date cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }
}

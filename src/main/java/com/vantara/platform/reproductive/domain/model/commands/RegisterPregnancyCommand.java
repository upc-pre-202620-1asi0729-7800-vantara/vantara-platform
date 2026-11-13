package com.vantara.platform.reproductive.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to register the pregnancy of an animal ("register pregnancy").
 *
 * @param animalId          identifier of the pregnant animal
 * @param confirmedOn       date the pregnancy was confirmed
 * @param expectedCalvingOn expected calving date, or {@code null} when unknown
 */
public record RegisterPregnancyCommand(String animalId, LocalDate confirmedOn, LocalDate expectedCalvingOn) {
    public RegisterPregnancyCommand {
        if (animalId == null || animalId.isBlank()) {
            throw new IllegalArgumentException("animalId cannot be null or blank");
        }
        if (confirmedOn == null) {
            throw new IllegalArgumentException("confirmedOn cannot be null");
        }
    }
}

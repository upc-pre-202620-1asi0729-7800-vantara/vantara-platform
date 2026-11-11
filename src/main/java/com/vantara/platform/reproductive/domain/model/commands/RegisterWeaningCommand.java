package com.vantara.platform.reproductive.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to register the weaning of a calf ("register weaning").
 *
 * @param recordId identifier of the reproductive record
 * @param weanedOn date of the weaning
 * @param weightKg weight of the calf at weaning in kilograms, or {@code null} when it was not measured
 */
public record RegisterWeaningCommand(Long recordId, LocalDate weanedOn, BigDecimal weightKg) {
    public RegisterWeaningCommand {
        if (recordId == null || recordId <= 0L) {
            throw new IllegalArgumentException("recordId cannot be null or less than 1");
        }
        if (weanedOn == null) {
            throw new IllegalArgumentException("weanedOn cannot be null");
        }
    }
}

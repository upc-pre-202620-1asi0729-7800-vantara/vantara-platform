package com.vantara.platform.reproductive.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to register the calving of a pregnancy ("register calving").
 *
 * @param recordId identifier of the reproductive record
 * @param calvedOn date of the calving
 * @param outcome  result of the calving
 * @param calf     data of the calf to register in livestock, or {@code null} when no calf is registered
 */
public record RegisterCalvingCommand(Long recordId, LocalDate calvedOn, String outcome, CalfRegistration calf) {
    public RegisterCalvingCommand {
        if (recordId == null || recordId <= 0L) {
            throw new IllegalArgumentException("recordId cannot be null or less than 1");
        }
        if (calvedOn == null) {
            throw new IllegalArgumentException("calvedOn cannot be null");
        }
        if (outcome == null || outcome.isBlank()) {
            throw new IllegalArgumentException("outcome cannot be null or blank");
        }
    }
}

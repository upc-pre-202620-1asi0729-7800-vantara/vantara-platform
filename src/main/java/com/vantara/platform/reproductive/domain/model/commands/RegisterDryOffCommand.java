package com.vantara.platform.reproductive.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to register the dry-off of a pregnant animal ("register dry-off").
 *
 * @param recordId identifier of the reproductive record
 * @param dryOffOn date of the dry-off
 */
public record RegisterDryOffCommand(Long recordId, LocalDate dryOffOn) {
    public RegisterDryOffCommand {
        if (recordId == null || recordId <= 0L) {
            throw new IllegalArgumentException("recordId cannot be null or less than 1");
        }
        if (dryOffOn == null) {
            throw new IllegalArgumentException("dryOffOn cannot be null");
        }
    }
}

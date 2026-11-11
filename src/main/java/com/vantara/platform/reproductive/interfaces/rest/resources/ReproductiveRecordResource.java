package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reproductive record resource: one pregnancy cycle with its calving, dry-off and weaning data.
 */
@Schema(
        name = "ReproductiveRecordResponse",
        description = "Reproductive record of one pregnancy cycle"
)
public record ReproductiveRecordResource(
        @Schema(description = "Record identifier", example = "1")
        Long id,

        @Schema(description = "Identifier of the pregnant animal (mother)", example = "12")
        Long animalId,

        @Schema(description = "Identifier of the calf born in the calving, null until a calf is registered", nullable = true)
        Long calfId,

        @Schema(description = "Date the pregnancy was confirmed", example = "2026-10-15", format = "date")
        LocalDate confirmedOn,

        @Schema(description = "Expected calving date", example = "2027-07-25", format = "date", nullable = true)
        LocalDate expectedCalvingOn,

        @Schema(description = "Date of the dry-off", example = "2027-05-20", format = "date", nullable = true)
        LocalDate dryOffOn,

        @Schema(description = "Date of the calving, which ends the pregnancy", example = "2027-07-30", format = "date", nullable = true)
        LocalDate calvedOn,

        @Schema(description = "Result of the calving", example = "Healthy calf", nullable = true)
        String outcome,

        @Schema(description = "Birth weight of the calf in kilograms", example = "38.5", nullable = true)
        BigDecimal birthWeightKg,

        @Schema(description = "Date of the weaning", example = "2028-01-30", format = "date", nullable = true)
        LocalDate weanedOn,

        @Schema(description = "Weight of the calf at weaning in kilograms", example = "180.0", nullable = true)
        BigDecimal weaningWeightKg,

        @Schema(description = "Lifecycle status", example = "confirmed", allowableValues = {"confirmed", "dry_off", "ended", "weaned"})
        String status
) {
}

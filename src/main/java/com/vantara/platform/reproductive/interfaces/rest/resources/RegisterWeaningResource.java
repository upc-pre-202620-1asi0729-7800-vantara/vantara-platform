package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource received to register the weaning of a calf.
 */
@Schema(
        name = "RegisterWeaningRequest",
        description = "Request payload to register the weaning of the calf of a calving",
        example = "{\"weanedOn\": \"2028-01-30\", \"weaningWeightKg\": 180.0}"
)
public record RegisterWeaningResource(
        @Schema(description = "Date of the weaning (ISO yyyy-MM-dd); between the calving and today",
                example = "2028-01-30", format = "date")
        LocalDate weanedOn,

        @Schema(description = "Weight of the calf at weaning in kilograms (greater than zero); optional",
                example = "180.0", nullable = true)
        BigDecimal weaningWeightKg
) {
}

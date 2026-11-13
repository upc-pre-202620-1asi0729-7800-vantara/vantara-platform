package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Resource received to register the calving of a pregnancy.
 */
@Schema(
        name = "RegisterCalvingRequest",
        description = "Request payload to register the calving that ends a pregnancy",
        example = "{\"calvedOn\": \"2027-07-30\", \"outcome\": \"Healthy calf\", "
                + "\"calf\": {\"name\": \"Luna\", \"earTag\": \"A-1042\", \"sex\": \"female\", \"weightKg\": 38.5}}"
)
public record RegisterCalvingResource(
        @Schema(description = "Date of the calving (ISO yyyy-MM-dd); between the confirmation and today",
                example = "2027-07-30", format = "date")
        LocalDate calvedOn,

        @Schema(description = "Result of the calving", example = "Healthy calf", maxLength = 120)
        String outcome,

        @Schema(description = "Calf to register in livestock; omit when no calf is registered", nullable = true)
        RegisterCalfResource calf
) {
}

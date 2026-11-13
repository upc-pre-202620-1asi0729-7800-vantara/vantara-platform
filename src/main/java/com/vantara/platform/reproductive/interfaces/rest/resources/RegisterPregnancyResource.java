package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Resource received to register the pregnancy of an animal.
 */
@Schema(
        name = "RegisterPregnancyRequest",
        description = "Request payload to register a confirmed pregnancy",
        example = "{\"animalId\": \"3f2b8c9e-5d41-4a7e-9c1a-0b6d2e8f7a14\", \"confirmedOn\": \"2026-10-15\", \"expectedCalvingOn\": \"2027-07-25\"}"
)
public record RegisterPregnancyResource(
        @Schema(description = "Identifier of the pregnant animal", example = "3f2b8c9e-5d41-4a7e-9c1a-0b6d2e8f7a14")
        String animalId,

        @Schema(description = "Date the pregnancy was confirmed (ISO yyyy-MM-dd); cannot be in the future",
                example = "2026-10-15", format = "date")
        LocalDate confirmedOn,

        @Schema(description = "Expected calving date (ISO yyyy-MM-dd); optional, cannot precede the confirmation",
                example = "2027-07-25", format = "date", nullable = true)
        LocalDate expectedCalvingOn
) {
}

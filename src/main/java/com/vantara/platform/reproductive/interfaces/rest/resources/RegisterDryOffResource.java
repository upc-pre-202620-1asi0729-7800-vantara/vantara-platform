package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Resource received to register the dry-off of a pregnant animal.
 */
@Schema(
        name = "RegisterDryOffRequest",
        description = "Request payload to register the dry-off of a pregnant animal",
        example = "{\"dryOffOn\": \"2027-05-20\"}"
)
public record RegisterDryOffResource(
        @Schema(description = "Date of the dry-off (ISO yyyy-MM-dd); between the confirmation and today",
                example = "2027-05-20", format = "date")
        LocalDate dryOffOn
) {
}

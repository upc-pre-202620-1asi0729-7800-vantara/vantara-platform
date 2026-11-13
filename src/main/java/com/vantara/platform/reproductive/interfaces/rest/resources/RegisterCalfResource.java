package com.vantara.platform.reproductive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Data of the calf registered together with a calving.
 */
@Schema(name = "RegisterCalfRequest", description = "Calf to register in livestock together with the calving")
public record RegisterCalfResource(
        @Schema(description = "Name of the calf", example = "Luna", maxLength = 100)
        String name,

        @Schema(description = "Ear tag that identifies the calf", example = "A-1042", maxLength = 30)
        String earTag,

        @Schema(description = "Sex of the calf", example = "female", allowableValues = {"female", "male", "hembra", "macho"})
        String sex,

        @Schema(description = "Birth weight in kilograms (greater than zero)", example = "38.5")
        BigDecimal weightKg
) {
}

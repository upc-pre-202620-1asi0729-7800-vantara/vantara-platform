
package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecordFeedingResource(
        @NotBlank String feedingPlanId,
        @NotBlank String planItemId,
        String animalId,
        @NotBlank String lotId,
        @NotNull LocalDateTime feedAt,
        @NotNull @DecimalMin("0") BigDecimal quantity,
        @NotBlank String recordedBy,
        String notes
) {
}

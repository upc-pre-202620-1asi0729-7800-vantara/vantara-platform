
package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import jakarta.validation.constraints.NotBlank;

public record LotResource(
        String id,
        @NotBlank String rancherId,
        @NotBlank String name,
        @NotBlank String purpose,
        @NotBlank String status
) {

    public static LotResource from(Lot lot) {
        return new LotResource(
                lot.getId(),
                lot.getRancherId(),
                lot.getName(),
                lot.getPurpose(),
                lot.getStatus()
        );
    }
}

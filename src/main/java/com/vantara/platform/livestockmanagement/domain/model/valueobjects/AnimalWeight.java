
package com.vantara.platform.livestockmanagement.domain.model.valueobjects;

import java.math.BigDecimal;

public record AnimalWeight(BigDecimal value) {

    public AnimalWeight {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Animal weight must be non-negative"
            );
        }
    }
}

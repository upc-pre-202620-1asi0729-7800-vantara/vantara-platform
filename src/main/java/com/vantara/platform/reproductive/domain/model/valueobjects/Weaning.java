package com.vantara.platform.reproductive.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data of the weaning of the calf born in a calving.
 *
 * @param weanedOn date of the weaning
 * @param weight   weight of the calf at weaning, or {@code null} when it was not measured
 */
public record Weaning(LocalDate weanedOn, WeightInKg weight) {
    public Weaning {
        Objects.requireNonNull(weanedOn, "Weaning date cannot be null");
    }
}

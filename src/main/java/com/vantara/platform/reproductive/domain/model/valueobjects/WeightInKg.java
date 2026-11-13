package com.vantara.platform.reproductive.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Weight measured in kilograms (birth weight of a calf or weight at weaning).
 *
 * @param kilograms the weight, greater than zero
 */
public record WeightInKg(BigDecimal kilograms) {
    private static final BigDecimal MAX_KILOGRAMS = new BigDecimal("2000");

    public WeightInKg {
        if (kilograms == null || kilograms.signum() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }
        if (kilograms.compareTo(MAX_KILOGRAMS) > 0) {
            throw new IllegalArgumentException("Weight cannot exceed %s kg".formatted(MAX_KILOGRAMS));
        }
    }

    /**
     * Creates a weight from an optional value.
     *
     * @param kilograms the weight or {@code null} when it was not measured
     * @return the weight, or {@code null} when no value was provided
     */
    public static WeightInKg ofNullable(BigDecimal kilograms) {
        return kilograms == null ? null : new WeightInKg(kilograms);
    }
}

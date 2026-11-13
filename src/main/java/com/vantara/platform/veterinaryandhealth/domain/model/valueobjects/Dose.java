package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Quantity of a medicine or vaccine applied to an animal, expressed with its unit.
 *
 * @param amount quantity, strictly positive
 * @param unit   unit of measure, for example ml or mg
 */
public record Dose(BigDecimal amount, String unit) {

    private static final int MAX_UNIT_LENGTH = 16;

    public Dose {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Dose amount must be greater than zero");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Dose unit cannot be blank");
        }
        unit = unit.trim();
        if (unit.length() > MAX_UNIT_LENGTH) {
            throw new IllegalArgumentException("Dose unit cannot exceed 16 characters");
        }
    }
}

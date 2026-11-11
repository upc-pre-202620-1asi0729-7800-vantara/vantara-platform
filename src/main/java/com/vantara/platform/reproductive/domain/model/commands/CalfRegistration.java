package com.vantara.platform.reproductive.domain.model.commands;

import com.vantara.platform.reproductive.domain.model.valueobjects.CalfSex;

import java.math.BigDecimal;

/**
 * Data of the calf registered together with a calving.
 *
 * @param name     name of the calf
 * @param earTag   ear tag that identifies the calf
 * @param sex      sex of the calf
 * @param weightKg birth weight in kilograms
 */
public record CalfRegistration(String name, String earTag, CalfSex sex, BigDecimal weightKg) {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EAR_TAG_LENGTH = 30;

    public CalfRegistration {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Calf name cannot be null or blank");
        }
        if (earTag == null || earTag.isBlank()) {
            throw new IllegalArgumentException("Calf ear tag cannot be null or blank");
        }
        name = name.trim();
        earTag = earTag.trim();
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Calf name cannot exceed %d characters".formatted(MAX_NAME_LENGTH));
        }
        if (earTag.length() > MAX_EAR_TAG_LENGTH) {
            throw new IllegalArgumentException("Calf ear tag cannot exceed %d characters".formatted(MAX_EAR_TAG_LENGTH));
        }
        if (sex == null) {
            throw new IllegalArgumentException("Calf sex cannot be null");
        }
        if (weightKg == null) {
            throw new IllegalArgumentException("Calf weight cannot be null");
        }
    }
}

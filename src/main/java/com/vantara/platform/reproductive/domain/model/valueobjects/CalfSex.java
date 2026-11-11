package com.vantara.platform.reproductive.domain.model.valueobjects;

import java.util.Locale;

/**
 * Sex of a calf registered with a calving.
 */
public enum CalfSex {
    FEMALE,
    MALE;

    /**
     * Parses the sex accepting the English and Spanish names used by clients.
     *
     * @param value {@code female}, {@code male}, {@code hembra} or {@code macho}, in any letter case
     * @return the matching sex
     * @throws IllegalArgumentException when the value is blank or unknown
     */
    public static CalfSex from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Calf sex cannot be null or blank");
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "female", "hembra" -> FEMALE;
            case "male", "macho" -> MALE;
            default -> throw new IllegalArgumentException("Calf sex must be female or male");
        };
    }
}

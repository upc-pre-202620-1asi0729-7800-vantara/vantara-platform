package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Full name provided by a person who contacts Vantara.
 *
 * @param value the full name
 */
public record FullName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 120;

    public FullName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be null or blank");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Full name length must be between %d and %d characters".formatted(MIN_LENGTH, MAX_LENGTH));
        }
    }
}

package com.vantara.platform.reproductive.domain.model.valueobjects;

/**
 * Result of a calving as described by the rancher (for example "Healthy calf").
 *
 * @param description the outcome text, not blank and at most 120 characters
 */
public record CalvingOutcome(String description) {
    private static final int MAX_LENGTH = 120;

    public CalvingOutcome {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Calving outcome cannot be null or blank");
        }
        description = description.trim();
        if (description.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Calving outcome cannot exceed %d characters".formatted(MAX_LENGTH));
        }
    }
}

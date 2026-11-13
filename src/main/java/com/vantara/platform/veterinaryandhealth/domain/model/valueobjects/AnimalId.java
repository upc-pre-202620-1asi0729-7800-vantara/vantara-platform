package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Logical reference to an animal owned by the livestock management context.
 * Veterinary records only keep this identifier; they never own the animal data.
 *
 * @param value identifier of the animal
 */
public record AnimalId(String value) {

    private static final int MAX_LENGTH = 36;

    public AnimalId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Animal id cannot be blank");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Animal id cannot exceed 36 characters");
        }
    }
}

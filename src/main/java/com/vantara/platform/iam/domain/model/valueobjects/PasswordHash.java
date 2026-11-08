package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Encrypted (hashed) password stored for a user.
 *
 * @param value the hash produced by the hashing service
 */
public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
    }

    @Override
    public String toString() {
        return "PasswordHash[value=****]";
    }
}

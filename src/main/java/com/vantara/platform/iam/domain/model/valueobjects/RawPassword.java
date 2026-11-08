package com.vantara.platform.iam.domain.model.valueobjects;

import java.nio.charset.StandardCharsets;

/**
 * Plain-text password received from a user, validated against the password policy.
 *
 * <p>It only exists long enough to be hashed; it must never be persisted or logged. The maximum
 * length is measured in UTF-8 bytes because BCrypt ignores everything after 72 bytes.</p>
 *
 * @param value the plain-text password
 */
public record RawPassword(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_BYTES = 72;

    public RawPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must have at least %d characters".formatted(MIN_LENGTH));
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new IllegalArgumentException("Password cannot exceed %d bytes".formatted(MAX_BYTES));
        }
        if (value.chars().noneMatch(Character::isLetter) || value.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Password must contain at least one letter and one digit");
        }
    }

    @Override
    public String toString() {
        return "RawPassword[value=****]";
    }
}

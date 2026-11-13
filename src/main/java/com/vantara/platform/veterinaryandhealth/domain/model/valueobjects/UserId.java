package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Logical reference to a user account owned by the IAM context (rancher or veterinarian).
 *
 * @param value numeric identifier of the user
 */
public record UserId(Long value) {

    public UserId {
        if (value == null || value <= 0L) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
    }
}

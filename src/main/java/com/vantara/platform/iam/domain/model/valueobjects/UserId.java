package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Identifier of a {@code User} aggregate, used to reference it from other aggregates.
 *
 * @param userId the numeric identifier
 */
public record UserId(Long userId) {
    public UserId {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}

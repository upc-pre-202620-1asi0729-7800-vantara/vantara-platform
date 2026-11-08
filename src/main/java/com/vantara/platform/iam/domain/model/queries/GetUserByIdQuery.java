package com.vantara.platform.iam.domain.model.queries;

/**
 * Query to retrieve the account data shown in the profile view of a user.
 *
 * @param userId identifier of the user
 */
public record GetUserByIdQuery(Long userId) {
    public GetUserByIdQuery {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}

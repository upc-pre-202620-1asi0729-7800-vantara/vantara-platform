package com.vantara.platform.iam.domain.model.queries;

/**
 * Query to retrieve a user by email address.
 *
 * @param email email address of the user
 */
public record GetUserByEmailQuery(String email) {
    public GetUserByEmailQuery {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
}

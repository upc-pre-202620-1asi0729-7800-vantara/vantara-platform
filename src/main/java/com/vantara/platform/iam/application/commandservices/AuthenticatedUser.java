package com.vantara.platform.iam.application.commandservices;

import com.vantara.platform.iam.domain.model.aggregates.User;

/**
 * Result of a successful sign-in: the user and the bearer token issued for it.
 *
 * @param user  the authenticated user
 * @param token the signed bearer token
 */
public record AuthenticatedUser(User user, String token) {
}

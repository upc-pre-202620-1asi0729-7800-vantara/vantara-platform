package com.vantara.platform.profile.domain.model.queries;

/**
 * Reads the profile of the account with that user id.
 *
 * @param userId identifier of the account in the IAM context
 */
public record GetProfileByUserIdQuery(Long userId) {
}

package com.vantara.platform.profilemanagement.domain.repositories;

import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;

import java.util.Optional;

/**
 * Persistence port of the profile bounded context.
 */
public interface ProfileRepository {

    /**
     * Inserts the profile when it is new, or updates it when it already has an id.
     */
    Profile save(Profile profile);

    /**
     * @return the profile of the account with that user id, or empty when it has none
     */
    Optional<Profile> findByUserId(Long userId);
}

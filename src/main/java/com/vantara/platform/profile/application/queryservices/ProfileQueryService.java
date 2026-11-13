package com.vantara.platform.profile.application.queryservices;

import com.vantara.platform.profile.domain.model.aggregates.Profile;
import com.vantara.platform.profile.domain.model.queries.GetProfileByUserIdQuery;

import java.util.Optional;

/**
 * Read-side contract of the profile queries.
 */
public interface ProfileQueryService {

    Optional<Profile> handle(GetProfileByUserIdQuery query);
}

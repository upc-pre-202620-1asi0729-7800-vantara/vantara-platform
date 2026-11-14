package com.vantara.platform.profilemanagement.application.queryservices;

import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.domain.model.queries.GetProfileByUserIdQuery;

import java.util.Optional;

/**
 * Read-side contract of the profile queries.
 */
public interface ProfileQueryService {

    Optional<Profile> handle(GetProfileByUserIdQuery query);
}

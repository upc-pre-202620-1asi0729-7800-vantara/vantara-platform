package com.vantara.platform.profilemanagement.application.internal.queryservices;

import com.vantara.platform.profilemanagement.application.queryservices.ProfileQueryService;
import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.domain.model.queries.GetProfileByUserIdQuery;
import com.vantara.platform.profilemanagement.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implements the profile queries of the read side.
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        if (query.userId() == null || query.userId() <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
        return profileRepository.findByUserId(query.userId());
    }
}

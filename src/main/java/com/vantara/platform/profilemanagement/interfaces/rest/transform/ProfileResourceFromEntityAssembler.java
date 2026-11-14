package com.vantara.platform.profilemanagement.interfaces.rest.transform;

import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.interfaces.rest.resources.ProfileResource;

/**
 * Translates a profile aggregate into its REST representation.
 */
public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    public static ProfileResource toResourceFromEntity(Profile profile) {
        return new ProfileResource(
                profile.getId(),
                profile.getUserId(),
                profile.getFullName(),
                profile.getOrganizationName(),
                profile.getPhone(),
                profile.getPhotoUrl(),
                profile.getPreferences().theme(),
                profile.getPreferences().locale(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}

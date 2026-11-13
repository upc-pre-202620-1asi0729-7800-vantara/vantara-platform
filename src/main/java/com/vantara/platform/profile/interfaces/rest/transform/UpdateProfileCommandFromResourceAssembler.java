package com.vantara.platform.profile.interfaces.rest.transform;

import com.vantara.platform.profile.domain.model.commands.CreateOrUpdateProfileCommand;
import com.vantara.platform.profile.interfaces.rest.resources.UpdateProfileResource;

/**
 * Translates an update request into its command.
 */
public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static CreateOrUpdateProfileCommand toCommandFromResource(Long userId, UpdateProfileResource resource) {
        return new CreateOrUpdateProfileCommand(
                userId,
                resource.fullName(),
                resource.organizationName(),
                resource.phone(),
                resource.photoUrl(),
                resource.theme(),
                resource.locale());
    }
}

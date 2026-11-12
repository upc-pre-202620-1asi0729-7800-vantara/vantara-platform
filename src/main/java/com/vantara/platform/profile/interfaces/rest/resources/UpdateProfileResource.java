package com.vantara.platform.profile.interfaces.rest.resources;

/**
 * Editable personal data and preferences sent by the authenticated user.
 */
public record UpdateProfileResource(
        String fullName,
        String organizationName,
        String phone,
        String photoUrl,
        String theme,
        String locale) {
}

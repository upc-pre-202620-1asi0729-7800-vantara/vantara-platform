package com.vantara.platform.profilemanagement.interfaces.rest.resources;

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

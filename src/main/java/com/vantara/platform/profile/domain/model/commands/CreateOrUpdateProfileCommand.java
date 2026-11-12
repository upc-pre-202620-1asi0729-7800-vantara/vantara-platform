package com.vantara.platform.profile.domain.model.commands;

/**
 * Creates the profile of an account, or replaces its personal data and preferences
 * when the account already has one.
 *
 * @param userId           identifier of the account in the IAM context
 * @param fullName         personal name of the account holder
 * @param organizationName optional organization the account belongs to
 * @param phone            optional contact phone
 * @param photoUrl         optional profile photo URL
 * @param theme            optional theme name; blank falls back to the default
 * @param locale           optional language code; blank falls back to the default
 */
public record CreateOrUpdateProfileCommand(
        Long userId,
        String fullName,
        String organizationName,
        String phone,
        String photoUrl,
        String theme,
        String locale) {
}

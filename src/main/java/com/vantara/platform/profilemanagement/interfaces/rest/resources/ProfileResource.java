package com.vantara.platform.profilemanagement.interfaces.rest.resources;

import java.time.Instant;

/**
 * Personal data and display preferences of an account. It never includes credentials;
 * the email address stays in the IAM context.
 */
public record ProfileResource(
        Long id,
        Long userId,
        String fullName,
        String organizationName,
        String phone,
        String photoUrl,
        String theme,
        String locale,
        Instant createdAt,
        Instant updatedAt) {
}

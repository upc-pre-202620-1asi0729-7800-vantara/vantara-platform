package com.vantara.platform.iam.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

/**
 * Account data returned by the API. It never includes the password or its hash.
 */
public record UserResource(
        Long id,
        String email,
        String status,
        List<String> roles,
        Instant lastSignInAt) {
}

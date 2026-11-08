package com.vantara.platform.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Data returned after a successful sign-in, with the bearer token.
 */
public record AuthenticatedUserResource(
        Long id,
        String email,
        List<String> roles,
        String token) {
}

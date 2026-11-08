package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

/**
 * Request body to sign in.
 */
public record SignInResource(
        @NotBlank @Email String email,
        @NotBlank String password) {
}

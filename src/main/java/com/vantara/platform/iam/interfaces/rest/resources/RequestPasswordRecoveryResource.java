package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

/**
 * Request body to start a password recovery.
 */
public record RequestPasswordRecoveryResource(
        @NotBlank @Email String email) {
}

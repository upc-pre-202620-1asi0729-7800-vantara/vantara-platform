package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

/**
 * Request body to receive a new verification code.
 */
public record SendVerificationCodeResource(
        @NotBlank @Email String email) {
}

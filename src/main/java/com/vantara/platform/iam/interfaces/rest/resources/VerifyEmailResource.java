package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Request body to verify an email address with the code received.
 */
public record VerifyEmailResource(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "must be exactly 6 digits") String code) {
}

package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body to set a new password with the recovery code.
 */
public record RecoverPasswordResource(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "must be exactly 6 digits") String code,
        @NotBlank @Size(max = 72) String newPassword) {
}

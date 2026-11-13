package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body to register a new account.
 */
public record SignUpResource(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 72) String password,
        @Schema(description = "ROLE_RANCHER (default), ROLE_VETERINARIAN or ROLE_FINAL_USER") String role) {
}

package com.vantara.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request body of the contact form.
 */
public record ContactRequestResource(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 1000) String message) {
}

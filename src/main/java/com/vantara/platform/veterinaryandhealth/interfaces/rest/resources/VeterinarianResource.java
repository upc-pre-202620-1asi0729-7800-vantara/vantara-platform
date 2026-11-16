
package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

/**
 * Resource representing a veterinarian available for appointment scheduling.
 */
public record VeterinarianResource(
        Long id,
        String fullName,
        String email
) {
}


package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to replace the mutable state of an existing animal.
 */
public record UpdateAnimalCommand(
        String id,
        String lotId,
        String name,
        String species,
        String breed,
        String sex,
        LocalDate birthDate,
        String motherId,
        String fatherId,
        BigDecimal weight,
        String status,
        String photoUrl,
        LocalDate weanedOn
) {

    public UpdateAnimalCommand {
        require(id, "id");
        require(lotId, "lotId");
        require(name, "name");
        require(species, "species");
        require(breed, "breed");
        require(sex, "sex");
        require(status, "status");

        if (birthDate == null) {
            throw new IllegalArgumentException(
                    "birthDate cannot be null"
            );
        }

        if (weight == null || weight.signum() < 0) {
            throw new IllegalArgumentException(
                    "weight must be non-negative"
            );
        }

        id = id.trim();
        lotId = lotId.trim();
        name = name.trim();
        species = species.trim();
        breed = breed.trim();
        sex = sex.trim();
        status = status.trim();

        motherId = optionalText(motherId);
        fatherId = optionalText(fatherId);
        photoUrl = optionalText(photoUrl);
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}

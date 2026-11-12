
package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to register an animal.
 */
public record CreateAnimalCommand(
        String rancherId,
        String lotId,
        String earTag,
        String name,
        String species,
        String breed,
        String sex,
        LocalDate birthDate,
        String motherId,
        String fatherId,
        BigDecimal weight,
        String status,
        LocalDate registeredAt,
        String photoUrl,
        LocalDate weanedOn
) {

    public CreateAnimalCommand {
        require(rancherId, "rancherId");
        require(lotId, "lotId");
        require(earTag, "earTag");
        require(name, "name");
        require(species, "species");
        require(breed, "breed");
        require(sex, "sex");
        require(status, "status");

        if (birthDate == null || registeredAt == null) {
            throw new IllegalArgumentException(
                    "birthDate and registeredAt cannot be null"
            );
        }

        if (weight == null || weight.signum() < 0) {
            throw new IllegalArgumentException(
                    "weight must be non-negative"
            );
        }

        rancherId = rancherId.trim();
        lotId = lotId.trim();
        earTag = earTag.trim();
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

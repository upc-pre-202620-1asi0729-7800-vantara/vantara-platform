package com.vantara.platform.reproductive.domain.model.valueobjects;

/**
 * Identifier of an animal of the livestock management context, used to reference it by id only.
 *
 * @param animalId the string (UUID) identifier assigned by livestock
 */
public record AnimalId(String animalId) {
    public AnimalId {
        if (animalId == null || animalId.isBlank()) {
            throw new IllegalArgumentException("Animal id cannot be null or blank");
        }
        animalId = animalId.trim();
    }
}

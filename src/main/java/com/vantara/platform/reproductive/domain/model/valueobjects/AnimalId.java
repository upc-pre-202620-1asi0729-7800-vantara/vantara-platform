package com.vantara.platform.reproductive.domain.model.valueobjects;

/**
 * Identifier of an animal of the livestock management context, used to reference it by id only.
 *
 * @param animalId the numeric identifier
 */
public record AnimalId(Long animalId) {
    public AnimalId {
        if (animalId == null || animalId <= 0L) {
            throw new IllegalArgumentException("Animal id cannot be null or less than 1");
        }
    }
}

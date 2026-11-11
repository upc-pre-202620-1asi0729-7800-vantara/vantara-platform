package com.vantara.platform.reproductive.domain.model.queries;

/**
 * Query to retrieve the reproductive history of one animal, ordered chronologically.
 *
 * @param animalId identifier of the animal
 */
public record GetReproductiveRecordsByAnimalIdQuery(Long animalId) {
    public GetReproductiveRecordsByAnimalIdQuery {
        if (animalId == null || animalId <= 0L) {
            throw new IllegalArgumentException("animalId cannot be null or less than 1");
        }
    }
}

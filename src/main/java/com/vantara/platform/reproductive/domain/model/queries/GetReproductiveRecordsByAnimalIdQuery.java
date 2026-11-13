package com.vantara.platform.reproductive.domain.model.queries;

/**
 * Query to retrieve the reproductive history of one animal, ordered chronologically.
 *
 * @param animalId identifier of the animal
 */
public record GetReproductiveRecordsByAnimalIdQuery(String animalId) {
    public GetReproductiveRecordsByAnimalIdQuery {
        if (animalId == null || animalId.isBlank()) {
            throw new IllegalArgumentException("animalId cannot be null or blank");
        }
    }
}

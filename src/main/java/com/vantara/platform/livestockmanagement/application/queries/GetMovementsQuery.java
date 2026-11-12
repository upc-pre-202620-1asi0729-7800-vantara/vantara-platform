
package com.vantara.platform.livestockmanagement.application.queries;

/**
 * Retrieves movement history, optionally restricted to one animal.
 */
public record GetMovementsQuery(
        String animalId
) {

    public GetMovementsQuery {
        if (animalId != null) {
            if (animalId.isBlank()) {
                throw new IllegalArgumentException(
                        "animalId cannot be blank when supplied"
                );
            }

            animalId = animalId.trim();
        }
    }
}

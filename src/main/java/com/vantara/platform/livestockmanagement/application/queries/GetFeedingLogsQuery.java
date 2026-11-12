
package com.vantara.platform.livestockmanagement.application.queries;

/**
 * Retrieves feeding logs, optionally filtered by lot or animal.
 */
public record GetFeedingLogsQuery(
        String lotId,
        String animalId
) {

    public GetFeedingLogsQuery {
        if (lotId != null) {
            if (lotId.isBlank()) {
                throw new IllegalArgumentException(
                        "lotId cannot be blank when supplied"
                );
            }

            lotId = lotId.trim();
        }

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

package com.vantara.platform.livestockmanagement.application.queries;

/** Retrieves feeding logs, optionally filtered by lot or animal. */
public record GetFeedingLogsQuery(String lotId, String animalId) {
    public GetFeedingLogsQuery {
        if (lotId != null && lotId.isBlank()) throw new IllegalArgumentException("lotId cannot be blank when supplied");
        if (animalId != null && animalId.isBlank()) throw new IllegalArgumentException("animalId cannot be blank when supplied");
    }
}

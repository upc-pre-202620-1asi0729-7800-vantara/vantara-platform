package com.vantara.platform.livestockmanagement.application.queries;

/** Retrieves one animal by identifier. */
public record GetAnimalByIdQuery(String id) {
    public GetAnimalByIdQuery { if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank"); }
}

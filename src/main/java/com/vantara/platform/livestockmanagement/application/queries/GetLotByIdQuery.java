package com.vantara.platform.livestockmanagement.application.queries;

/** Retrieves one ranch lot by identifier. */
public record GetLotByIdQuery(String id) {
    public GetLotByIdQuery { if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank"); }
}

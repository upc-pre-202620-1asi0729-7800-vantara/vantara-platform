package com.vantara.platform.reproductive.domain.model.queries;

/**
 * Query to retrieve one reproductive record.
 *
 * @param recordId identifier of the record
 */
public record GetReproductiveRecordByIdQuery(Long recordId) {
    public GetReproductiveRecordByIdQuery {
        if (recordId == null || recordId <= 0L) {
            throw new IllegalArgumentException("recordId cannot be null or less than 1");
        }
    }
}

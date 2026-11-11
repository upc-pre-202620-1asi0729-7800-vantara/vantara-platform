package com.vantara.platform.reproductive.application.queryservices;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordByIdQuery;

import java.util.Optional;

/**
 * Application service contract for reproductive record read queries.
 */
public interface ReproductiveRecordQueryService {

    /**
     * Handles retrieval of a reproductive record by id.
     *
     * @param query record-id query
     * @return the matching record, if found
     * @see GetReproductiveRecordByIdQuery
     */
    Optional<ReproductiveRecord> handle(GetReproductiveRecordByIdQuery query);
}

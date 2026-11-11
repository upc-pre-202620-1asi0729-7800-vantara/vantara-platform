package com.vantara.platform.reproductive.application.queryservices;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.queries.GetAllReproductiveRecordsQuery;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordByIdQuery;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordsByAnimalIdQuery;

import java.util.List;
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

    /**
     * Handles retrieval of the reproductive history of an animal.
     *
     * @param query animal-id query
     * @return the animal's records ordered chronologically; empty when it has no reproductive events
     * @see GetReproductiveRecordsByAnimalIdQuery
     */
    List<ReproductiveRecord> handle(GetReproductiveRecordsByAnimalIdQuery query);

    /**
     * Handles retrieval of every reproductive record.
     *
     * @param query query marker
     * @return all records ordered chronologically
     * @see GetAllReproductiveRecordsQuery
     */
    List<ReproductiveRecord> handle(GetAllReproductiveRecordsQuery query);
}

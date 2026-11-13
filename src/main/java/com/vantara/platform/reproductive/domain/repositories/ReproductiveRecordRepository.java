package com.vantara.platform.reproductive.domain.repositories;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for {@link ReproductiveRecord} aggregates.
 */
public interface ReproductiveRecordRepository {

    ReproductiveRecord save(ReproductiveRecord record);

    Optional<ReproductiveRecord> findById(Long id);

    /**
     * @return every record, ordered chronologically by confirmation date
     */
    List<ReproductiveRecord> findAll();

    /**
     * @return the records of one animal, ordered chronologically by confirmation date
     */
    List<ReproductiveRecord> findAllByAnimalId(AnimalId animalId);

    /**
     * @return {@code true} when the animal has a pregnancy that has not ended in a calving yet
     */
    boolean existsPregnancyInProgressByAnimalId(AnimalId animalId);
}

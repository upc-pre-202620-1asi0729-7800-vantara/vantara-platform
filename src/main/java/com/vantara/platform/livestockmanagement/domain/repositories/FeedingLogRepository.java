package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingLog;
import java.util.List;

/** Persistence port for completed feeding records. */
public interface FeedingLogRepository {
    FeedingLog save(FeedingLog log);
    List<FeedingLog> findAll();
    List<FeedingLog> findAllByLotId(String lotId);
    List<FeedingLog> findAllByAnimalId(String animalId);
}

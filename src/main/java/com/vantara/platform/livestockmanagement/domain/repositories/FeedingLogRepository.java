
package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;

import java.util.Collection;
import java.util.List;

public interface FeedingLogRepository {

    FeedingLog save(FeedingLog log);

    List<FeedingLog> findAll();

    List<FeedingLog> findAllByLotIds(Collection<String> lotIds);

    List<FeedingLog> findAllByLotId(String lotId);

    List<FeedingLog> findAllByAnimalId(String animalId);

    List<FeedingLog> findAllByLotIdAndAnimalId(String lotId, String animalId);
}

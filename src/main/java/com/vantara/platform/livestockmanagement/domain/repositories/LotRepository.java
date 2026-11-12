
package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for ranch lots.
 */
public interface LotRepository {

    Lot save(Lot lot);

    Optional<Lot> findById(String id);

    Optional<Lot> findByIdAndRancherId(String id, String rancherId);

    List<Lot> findAll();

    List<Lot> findAllByRancherId(String rancherId);
}

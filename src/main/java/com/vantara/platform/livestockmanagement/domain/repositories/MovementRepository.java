
package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;

import java.util.List;

public interface MovementRepository {

    Movement save(Movement movement);

    List<Movement> findAll();

    List<Movement> findAllByAnimalId(String animalId);

    List<Movement> findAllByOriginLotIdOrDestinationLotId(
            String lotId
    );
}

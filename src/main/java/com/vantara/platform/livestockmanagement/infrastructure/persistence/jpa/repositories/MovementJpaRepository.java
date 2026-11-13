
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.MovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovementJpaRepository extends JpaRepository<MovementJpaEntity, String> {

    List<MovementJpaEntity> findAllByAnimalId(String animalId);

    List<MovementJpaEntity> findAllByOriginLotIdOrDestinationLotId(
            String originLotId,
            String destinationLotId
    );
}

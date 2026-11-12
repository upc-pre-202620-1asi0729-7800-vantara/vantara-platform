
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.MovementJpaEntity;

public class MovementMapper {

    private MovementMapper() {
    }

    public static MovementJpaEntity toEntity(Movement movement) {
        return new MovementJpaEntity(
                movement.getId(),
                movement.getAnimalId(),
                movement.getOriginLotId(),
                movement.getDestinationLotId(),
                movement.getMovementType(),
                movement.getOccurredAt(),
                movement.getReason(),
                movement.getRecordedBy()
        );
    }

    public static Movement toDomain(MovementJpaEntity entity) {
        return Movement.record(
                entity.getId(),
                entity.getAnimalId(),
                entity.getOriginLotId(),
                entity.getDestinationLotId(),
                entity.getMovementType(),
                entity.getOccurredAt(),
                entity.getReason(),
                entity.getRecordedBy()
        );
    }
}

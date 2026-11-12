
package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record MovementResource(
        String id,
        @NotBlank String animalId,
        String originLotId,
        @NotBlank String destinationLotId,
        @NotBlank String movementType,
        @NotNull LocalDateTime occurredAt,
        @NotBlank String reason,
        @NotBlank String recordedBy
) {

    public static MovementResource from(Movement movement) {
        return new MovementResource(
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
}

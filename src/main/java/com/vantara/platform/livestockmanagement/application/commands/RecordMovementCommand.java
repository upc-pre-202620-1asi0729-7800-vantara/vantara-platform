
package com.vantara.platform.livestockmanagement.application.commands;

import java.time.LocalDateTime;

/**
 * Request to record the assignment or transfer of an animal.
 */
public record RecordMovementCommand(
        String animalId,
        String originLotId,
        String destinationLotId,
        String movementType,
        LocalDateTime occurredAt,
        String reason,
        String recordedBy
) {

    public RecordMovementCommand {
        require(animalId, "animalId");
        require(destinationLotId, "destinationLotId");
        require(movementType, "movementType");
        require(reason, "reason");
        require(recordedBy, "recordedBy");

        if (occurredAt == null) {
            throw new IllegalArgumentException(
                    "occurredAt cannot be null"
            );
        }

        animalId = animalId.trim();
        destinationLotId = destinationLotId.trim();
        movementType = movementType.trim();
        reason = reason.trim();
        recordedBy = recordedBy.trim();

        originLotId = optionalText(originLotId);
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}

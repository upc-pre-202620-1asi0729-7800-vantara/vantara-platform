package com.vantara.platform.livestockmanagement.application.commands;

import java.time.LocalDateTime;

/** Request to record the assignment or transfer of an animal. */
public record RecordMovementCommand(String animalId, String originLotId, String destinationLotId, String movementType,
                                    LocalDateTime occurredAt, String reason, String recordedBy) {
    public RecordMovementCommand {
        require(animalId, "animalId"); require(destinationLotId, "destinationLotId"); require(movementType, "movementType");
        require(reason, "reason"); require(recordedBy, "recordedBy");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
    }
    private static void require(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank"); }
}

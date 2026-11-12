
package com.vantara.platform.livestockmanagement.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record of assigning or transferring an animal between lots.
 */
public final class Movement extends AbstractDomainAggregateRoot<Movement> {

    private final String id;
    private final String animalId;
    private final String originLotId;
    private final String destinationLotId;
    private final String movementType;
    private final LocalDateTime occurredAt;
    private final String reason;
    private final String recordedBy;

    private Movement(
            String id,
            String animalId,
            String originLotId,
            String destinationLotId,
            String movementType,
            LocalDateTime occurredAt,
            String reason,
            String recordedBy
    ) {
        this.id = required(id, "Movement id");
        this.animalId = required(animalId, "Animal id");
        this.originLotId = blankToNull(originLotId);
        this.destinationLotId = required(destinationLotId, "Destination lot id");
        this.movementType = required(movementType, "Movement type");
        this.occurredAt = Objects.requireNonNull(
                occurredAt, "Movement date cannot be null"
        );
        this.reason = required(reason, "Movement reason");
        this.recordedBy = required(recordedBy, "Recorded by");

        if (this.destinationLotId.equals(this.originLotId)) {
            throw new IllegalArgumentException(
                    "Origin and destination lots must differ"
            );
        }
    }

    public static Movement record(
            String id,
            String animalId,
            String originLotId,
            String destinationLotId,
            String movementType,
            LocalDateTime occurredAt,
            String reason,
            String recordedBy
    ) {
        return new Movement(
                id, animalId, originLotId, destinationLotId,
                movementType, occurredAt, reason, recordedBy
        );
    }

    private static String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getId() { return id; }
    public String getAnimalId() { return animalId; }
    public String getOriginLotId() { return originLotId; }
    public String getDestinationLotId() { return destinationLotId; }
    public String getMovementType() { return movementType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getReason() { return reason; }
    public String getRecordedBy() { return recordedBy; }
}

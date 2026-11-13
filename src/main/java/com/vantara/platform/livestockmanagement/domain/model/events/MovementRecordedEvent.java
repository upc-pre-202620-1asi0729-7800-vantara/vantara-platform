package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when the movement of an animal between lots is recorded ("movement recorded").
 *
 * @param movementId       identifier of the movement
 * @param animalId         identifier of the moved animal
 * @param originLotId      lot the animal left, or {@code null} for a first assignment
 * @param destinationLotId lot the animal moved to
 * @param movementType     kind of movement
 * @param occurredAt       instant at which the movement was recorded
 */
public record MovementRecordedEvent(
        String movementId,
        String animalId,
        String originLotId,
        String destinationLotId,
        String movementType,
        Instant occurredAt
) implements DomainEvent {
}

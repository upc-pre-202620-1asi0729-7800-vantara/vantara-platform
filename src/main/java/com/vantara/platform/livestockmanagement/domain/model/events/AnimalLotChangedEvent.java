package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when an animal ends up in a different lot ("animal lot changed").
 *
 * @param animalId      identifier of the animal
 * @param rancherId     rancher that owns the animal
 * @param previousLotId lot the animal left
 * @param newLotId      lot the animal now belongs to
 * @param occurredAt    instant at which the change happened
 */
public record AnimalLotChangedEvent(
        String animalId,
        String rancherId,
        String previousLotId,
        String newLotId,
        Instant occurredAt
) implements DomainEvent {
}

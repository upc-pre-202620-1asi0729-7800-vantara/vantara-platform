package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when an animal is removed from the inventory ("animal removed").
 *
 * @param animalId   identifier of the removed animal
 * @param rancherId  rancher that owned the animal
 * @param lotId      lot the animal belonged to when removed
 * @param occurredAt instant at which the removal happened
 */
public record AnimalRemovedEvent(
        String animalId,
        String rancherId,
        String lotId,
        Instant occurredAt
) implements DomainEvent {
}

package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when an animal is registered in a rancher's inventory ("animal registered").
 *
 * @param animalId   identifier of the new animal
 * @param rancherId  rancher that owns the animal
 * @param lotId      lot the animal was assigned to
 * @param earTag     ear tag identifying the animal
 * @param species    animal species
 * @param sex        animal sex
 * @param motherId   mother animal id, or {@code null} when unknown
 * @param fatherId   father animal id, or {@code null} when unknown
 * @param occurredAt instant at which the registration happened
 */
public record AnimalRegisteredEvent(
        String animalId,
        String rancherId,
        String lotId,
        String earTag,
        String species,
        String sex,
        String motherId,
        String fatherId,
        Instant occurredAt
) implements DomainEvent {
}

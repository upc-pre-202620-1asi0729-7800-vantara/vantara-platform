package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when an animal is weaned for the first time ("animal weaned").
 *
 * @param animalId   identifier of the weaned animal
 * @param rancherId  rancher that owns the animal
 * @param motherId   mother animal id, or {@code null} when unknown
 * @param weanedOn   weaning date
 * @param occurredAt instant at which the weaning was recorded
 */
public record AnimalWeanedEvent(
        String animalId,
        String rancherId,
        String motherId,
        LocalDate weanedOn,
        Instant occurredAt
) implements DomainEvent {
}

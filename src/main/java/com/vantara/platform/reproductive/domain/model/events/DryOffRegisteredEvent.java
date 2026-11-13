package com.vantara.platform.reproductive.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when the dry-off of a pregnant animal is registered ("dry-off registered").
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the animal
 * @param dryOffOn   date of the dry-off
 * @param occurredAt instant at which the registration happened
 */
public record DryOffRegisteredEvent(
        Long recordId,
        String animalId,
        LocalDate dryOffOn,
        Instant occurredAt
) implements DomainEvent {
}

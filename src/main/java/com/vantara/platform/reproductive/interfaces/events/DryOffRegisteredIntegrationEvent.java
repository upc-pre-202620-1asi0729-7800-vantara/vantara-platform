package com.vantara.platform.reproductive.interfaces.events;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published by the {@code reproductive} context when a dry-off is registered.
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the animal
 * @param dryOffOn   date of the dry-off
 * @param occurredAt instant at which the registration happened
 */
public record DryOffRegisteredIntegrationEvent(
        Long recordId,
        String animalId,
        LocalDate dryOffOn,
        Instant occurredAt
) {
}

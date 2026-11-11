package com.vantara.platform.reproductive.interfaces.events;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published by the {@code reproductive} context when a calving is registered.
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the mother
 * @param calfId     identifier of the calf, or {@code null} when no calf was registered
 * @param calvedOn   date of the calving
 * @param outcome    result of the calving
 * @param occurredAt instant at which the registration happened
 */
public record BirthRegisteredIntegrationEvent(
        Long recordId,
        Long animalId,
        Long calfId,
        LocalDate calvedOn,
        String outcome,
        Instant occurredAt
) {
}

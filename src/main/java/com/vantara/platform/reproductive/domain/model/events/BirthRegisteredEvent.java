package com.vantara.platform.reproductive.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when the calving of a pregnancy is registered ("birth registered").
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the mother
 * @param calfId     identifier of the calf, or {@code null} when no calf was registered
 * @param calvedOn   date of the calving
 * @param outcome    result of the calving
 * @param occurredAt instant at which the registration happened
 */
public record BirthRegisteredEvent(
        Long recordId,
        Long animalId,
        Long calfId,
        LocalDate calvedOn,
        String outcome,
        Instant occurredAt
) implements DomainEvent {
}

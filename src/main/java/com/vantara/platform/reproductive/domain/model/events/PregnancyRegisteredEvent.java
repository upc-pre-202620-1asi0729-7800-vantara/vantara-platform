package com.vantara.platform.reproductive.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when a pregnancy is confirmed and its reproductive record is created ("pregnancy registered").
 *
 * @param recordId          identifier of the reproductive record
 * @param animalId          identifier of the pregnant animal
 * @param confirmedOn       date the pregnancy was confirmed
 * @param expectedCalvingOn expected calving date, or {@code null} when unknown
 * @param occurredAt        instant at which the registration happened
 */
public record PregnancyRegisteredEvent(
        Long recordId,
        String animalId,
        LocalDate confirmedOn,
        LocalDate expectedCalvingOn,
        Instant occurredAt
) implements DomainEvent {
}

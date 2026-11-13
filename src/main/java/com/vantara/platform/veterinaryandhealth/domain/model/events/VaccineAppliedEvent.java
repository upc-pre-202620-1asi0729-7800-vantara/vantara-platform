package com.vantara.platform.veterinaryandhealth.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when a vaccine dose is applied to an animal.
 *
 * @param vaccineId   identifier of the vaccine record
 * @param animalId    identifier of the vaccinated animal
 * @param nextDoseAt  date of the next dose, or null when there is none
 * @param occurredAt  instant at which the dose was registered
 */
public record VaccineAppliedEvent(
        String vaccineId,
        String animalId,
        LocalDate nextDoseAt,
        Instant occurredAt) implements DomainEvent {
}

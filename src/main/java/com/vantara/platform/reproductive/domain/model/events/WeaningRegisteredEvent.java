package com.vantara.platform.reproductive.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when the weaning of a calf is registered ("weaning registered").
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the mother
 * @param calfId     identifier of the weaned calf
 * @param weanedOn   date of the weaning
 * @param occurredAt instant at which the registration happened
 */
public record WeaningRegisteredEvent(
        Long recordId,
        Long animalId,
        Long calfId,
        LocalDate weanedOn,
        Instant occurredAt
) implements DomainEvent {
}

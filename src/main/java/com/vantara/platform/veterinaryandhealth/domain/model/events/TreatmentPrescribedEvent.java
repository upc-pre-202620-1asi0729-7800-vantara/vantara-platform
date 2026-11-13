package com.vantara.platform.veterinaryandhealth.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a treatment is prescribed to an animal.
 *
 * @param treatmentId  identifier of the treatment
 * @param animalId     identifier of the animal being treated
 * @param medicationId identifier of the prescribed medication
 * @param occurredAt   instant at which the treatment was prescribed
 */
public record TreatmentPrescribedEvent(
        String treatmentId,
        String animalId,
        String medicationId,
        Instant occurredAt) implements DomainEvent {
}

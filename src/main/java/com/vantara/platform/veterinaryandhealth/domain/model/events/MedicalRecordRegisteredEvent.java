package com.vantara.platform.veterinaryandhealth.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a clinical record is registered for an animal.
 *
 * @param medicalRecordId identifier of the clinical record
 * @param animalId        identifier of the animal diagnosed
 * @param severity        severity of the diagnosis
 * @param healthStatus    resulting health status of the animal
 * @param occurredAt      instant at which the record was registered
 */
public record MedicalRecordRegisteredEvent(
        String medicalRecordId,
        String animalId,
        String severity,
        String healthStatus,
        Instant occurredAt) implements DomainEvent {
}

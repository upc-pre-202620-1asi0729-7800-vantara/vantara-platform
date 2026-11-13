package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalRegisteredEvent;

import java.time.Instant;

/**
 * Integration event published to other bounded contexts when an animal is registered in a rancher inventory.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param animalId identifier of the new animal
 * @param rancherId rancher that owns the animal
 * @param lotId lot the animal was assigned to
 * @param earTag ear tag identifying the animal
 * @param species animal species
 * @param sex animal sex
 * @param motherId mother animal id, or null when unknown
 * @param fatherId father animal id, or null when unknown
 * @param occurredAt instant at which the fact happened in the domain
 */
public record AnimalRegisteredIntegrationEvent(
        String animalId,
        String rancherId,
        String lotId,
        String earTag,
        String species,
        String sex,
        String motherId,
        String fatherId,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static AnimalRegisteredIntegrationEvent from(AnimalRegisteredEvent event) {
        return new AnimalRegisteredIntegrationEvent(
                event.animalId(),
                event.rancherId(),
                event.lotId(),
                event.earTag(),
                event.species(),
                event.sex(),
                event.motherId(),
                event.fatherId(),
                event.occurredAt()
        );
    }
}

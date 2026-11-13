package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalLotChangedEvent;

import java.time.Instant;

/**
 * Integration event published to other bounded contexts when an animal ends up in a different lot.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param animalId identifier of the animal
 * @param rancherId rancher that owns the animal
 * @param previousLotId lot the animal left
 * @param newLotId lot the animal now belongs to
 * @param occurredAt instant at which the fact happened in the domain
 */
public record AnimalLotChangedIntegrationEvent(
        String animalId,
        String rancherId,
        String previousLotId,
        String newLotId,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static AnimalLotChangedIntegrationEvent from(AnimalLotChangedEvent event) {
        return new AnimalLotChangedIntegrationEvent(
                event.animalId(),
                event.rancherId(),
                event.previousLotId(),
                event.newLotId(),
                event.occurredAt()
        );
    }
}

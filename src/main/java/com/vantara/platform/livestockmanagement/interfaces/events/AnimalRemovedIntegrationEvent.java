package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalRemovedEvent;

import java.time.Instant;

/**
 * Integration event published to other bounded contexts when an animal is removed from the inventory.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param animalId identifier of the removed animal
 * @param rancherId rancher that owned the animal
 * @param lotId lot the animal belonged to when removed
 * @param occurredAt instant at which the fact happened in the domain
 */
public record AnimalRemovedIntegrationEvent(
        String animalId,
        String rancherId,
        String lotId,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static AnimalRemovedIntegrationEvent from(AnimalRemovedEvent event) {
        return new AnimalRemovedIntegrationEvent(
                event.animalId(),
                event.rancherId(),
                event.lotId(),
                event.occurredAt()
        );
    }
}

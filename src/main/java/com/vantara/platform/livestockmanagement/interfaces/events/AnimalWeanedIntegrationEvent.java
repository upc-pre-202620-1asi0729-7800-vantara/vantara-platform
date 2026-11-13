package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalWeanedEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published to other bounded contexts when an animal is weaned for the first time.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param animalId identifier of the weaned animal
 * @param rancherId rancher that owns the animal
 * @param motherId mother animal id, or null when unknown
 * @param weanedOn weaning date
 * @param occurredAt instant at which the fact happened in the domain
 */
public record AnimalWeanedIntegrationEvent(
        String animalId,
        String rancherId,
        String motherId,
        LocalDate weanedOn,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static AnimalWeanedIntegrationEvent from(AnimalWeanedEvent event) {
        return new AnimalWeanedIntegrationEvent(
                event.animalId(),
                event.rancherId(),
                event.motherId(),
                event.weanedOn(),
                event.occurredAt()
        );
    }
}

package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.MovementRecordedEvent;

import java.time.Instant;

/**
 * Integration event published to other bounded contexts when the movement of an animal between lots is recorded.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param movementId identifier of the movement
 * @param animalId identifier of the moved animal
 * @param originLotId lot the animal left, or null for a first assignment
 * @param destinationLotId lot the animal moved to
 * @param movementType kind of movement
 * @param occurredAt instant at which the fact happened in the domain
 */
public record MovementRecordedIntegrationEvent(
        String movementId,
        String animalId,
        String originLotId,
        String destinationLotId,
        String movementType,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static MovementRecordedIntegrationEvent from(MovementRecordedEvent event) {
        return new MovementRecordedIntegrationEvent(
                event.movementId(),
                event.animalId(),
                event.originLotId(),
                event.destinationLotId(),
                event.movementType(),
                event.occurredAt()
        );
    }
}

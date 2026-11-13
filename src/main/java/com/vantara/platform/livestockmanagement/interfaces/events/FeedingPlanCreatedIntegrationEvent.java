package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.FeedingPlanCreatedEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published to other bounded contexts when a feeding plan is created.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param feedingPlanId identifier of the new plan
 * @param lotId lot the plan feeds
 * @param animalId animal the plan feeds, or null when it targets the whole lot
 * @param startsOn first day of the plan
 * @param endsOn last day of the plan
 * @param occurredAt instant at which the fact happened in the domain
 */
public record FeedingPlanCreatedIntegrationEvent(
        String feedingPlanId,
        String lotId,
        String animalId,
        LocalDate startsOn,
        LocalDate endsOn,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static FeedingPlanCreatedIntegrationEvent from(FeedingPlanCreatedEvent event) {
        return new FeedingPlanCreatedIntegrationEvent(
                event.feedingPlanId(),
                event.lotId(),
                event.animalId(),
                event.startsOn(),
                event.endsOn(),
                event.occurredAt()
        );
    }
}

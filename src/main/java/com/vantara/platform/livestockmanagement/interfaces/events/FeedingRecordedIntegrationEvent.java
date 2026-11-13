package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.FeedingRecordedEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Integration event published to other bounded contexts when a completed feeding activity is recorded.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param feedingLogId identifier of the feeding log
 * @param planItemId plan item that was fed
 * @param lotId lot that was fed
 * @param animalId animal that was fed, or null for a lot-wide feeding
 * @param feedName feed given
 * @param quantity quantity given
 * @param unit unit of the quantity
 * @param occurredAt instant at which the fact happened in the domain
 */
public record FeedingRecordedIntegrationEvent(
        String feedingLogId,
        String planItemId,
        String lotId,
        String animalId,
        String feedName,
        BigDecimal quantity,
        String unit,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static FeedingRecordedIntegrationEvent from(FeedingRecordedEvent event) {
        return new FeedingRecordedIntegrationEvent(
                event.feedingLogId(),
                event.planItemId(),
                event.lotId(),
                event.animalId(),
                event.feedName(),
                event.quantity(),
                event.unit(),
                event.occurredAt()
        );
    }
}

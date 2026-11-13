package com.vantara.platform.livestockmanagement.interfaces.events;

import com.vantara.platform.livestockmanagement.domain.model.events.LotRegisteredEvent;

import java.time.Instant;

/**
 * Integration event published to other bounded contexts when a lot is registered.
 *
 * <p>This is the published language of the livestock context: other contexts listen to this
 * record and never to the internal domain event.</p>
 *
 * @param lotId identifier of the new lot
 * @param rancherId rancher that owns the lot
 * @param name lot name
 * @param occurredAt instant at which the fact happened in the domain
 */
public record LotRegisteredIntegrationEvent(
        String lotId,
        String rancherId,
        String name,
        Instant occurredAt
) {

    /**
     * Translates the internal domain event into its published form.
     *
     * @param event the domain event raised by the livestock context
     * @return the integration event
     */
    public static LotRegisteredIntegrationEvent from(LotRegisteredEvent event) {
        return new LotRegisteredIntegrationEvent(
                event.lotId(),
                event.rancherId(),
                event.name(),
                event.occurredAt()
        );
    }
}

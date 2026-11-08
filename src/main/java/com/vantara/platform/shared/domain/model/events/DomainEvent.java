package com.vantara.platform.shared.domain.model.events;

import java.time.Instant;

/**
 * Marker contract for domain events raised by aggregates.
 */
public interface DomainEvent {

    /**
     * @return the instant at which the event occurred
     */
    Instant occurredAt();
}

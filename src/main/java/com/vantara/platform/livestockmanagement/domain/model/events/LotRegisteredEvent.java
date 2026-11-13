package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a lot is registered ("lot registered").
 *
 * @param lotId      identifier of the new lot
 * @param rancherId  rancher that owns the lot
 * @param name       lot name
 * @param occurredAt instant at which the registration happened
 */
public record LotRegisteredEvent(
        String lotId,
        String rancherId,
        String name,
        Instant occurredAt
) implements DomainEvent {
}

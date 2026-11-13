package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Raised when a feeding plan is created ("feeding plan created").
 *
 * @param feedingPlanId identifier of the new plan
 * @param lotId         lot the plan feeds
 * @param animalId      animal the plan feeds, or {@code null} when it targets the whole lot
 * @param startsOn      first day of the plan
 * @param endsOn        last day of the plan
 * @param occurredAt    instant at which the plan was created
 */
public record FeedingPlanCreatedEvent(
        String feedingPlanId,
        String lotId,
        String animalId,
        LocalDate startsOn,
        LocalDate endsOn,
        Instant occurredAt
) implements DomainEvent {
}

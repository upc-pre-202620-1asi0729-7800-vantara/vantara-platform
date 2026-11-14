package com.vantara.platform.reports.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Read model of a feeding plan, as published by the livestock context.
 *
 * @param id        identifier of the plan
 * @param lotId     lot the plan belongs to
 * @param animalId  individual animal the plan targets, or null when it targets the whole lot
 * @param name      name of the plan
 * @param startsOn  first day of the plan
 */
public record FeedingPlanSnapshot(String id, String lotId, String animalId, String name, LocalDate startsOn) {

    public FeedingPlanSnapshot {
        Objects.requireNonNull(id, "Feeding plan id cannot be null");
        Objects.requireNonNull(lotId, "Lot id cannot be null");
        Objects.requireNonNull(name, "Plan name cannot be null");
        Objects.requireNonNull(startsOn, "Start date cannot be null");
    }
}

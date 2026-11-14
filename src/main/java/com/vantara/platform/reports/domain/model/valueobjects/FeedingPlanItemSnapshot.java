package com.vantara.platform.reports.domain.model.valueobjects;

import java.util.Objects;

/**
 * Read model of an ingredient of a feeding plan. Reports only need it to link a feeding record to its plan.
 *
 * @param id            identifier of the plan item
 * @param feedingPlanId plan the item belongs to
 */
public record FeedingPlanItemSnapshot(String id, String feedingPlanId) {

    public FeedingPlanItemSnapshot {
        Objects.requireNonNull(id, "Plan item id cannot be null");
        Objects.requireNonNull(feedingPlanId, "Feeding plan id cannot be null");
    }
}

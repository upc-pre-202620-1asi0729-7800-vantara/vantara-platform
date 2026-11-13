
package com.vantara.platform.livestockmanagement.application.queries;

/**
 * Retrieves the items belonging to a feeding plan.
 */
public record GetFeedingPlanItemsQuery(
        String feedingPlanId
) {

    public GetFeedingPlanItemsQuery {
        if (feedingPlanId == null || feedingPlanId.isBlank()) {
            throw new IllegalArgumentException(
                    "feedingPlanId cannot be blank"
            );
        }

        feedingPlanId = feedingPlanId.trim();
    }
}

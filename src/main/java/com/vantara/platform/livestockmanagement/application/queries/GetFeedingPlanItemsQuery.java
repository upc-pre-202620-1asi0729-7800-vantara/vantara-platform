package com.vantara.platform.livestockmanagement.application.queries;

/** Retrieves plan items, optionally restricted to one feeding plan. */
public record GetFeedingPlanItemsQuery(String feedingPlanId) {
    public GetFeedingPlanItemsQuery {
        if (feedingPlanId != null && feedingPlanId.isBlank()) throw new IllegalArgumentException("feedingPlanId cannot be blank when supplied");
    }
}

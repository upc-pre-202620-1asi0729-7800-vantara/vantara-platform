package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;

/** Request to add a feed item to an existing feeding plan. */
public record CreateFeedingPlanItemCommand(String feedingPlanId, String feedName, BigDecimal dailyQuantity, String unit) {
    public CreateFeedingPlanItemCommand {
        require(feedingPlanId, "feedingPlanId"); require(feedName, "feedName"); require(unit, "unit");
        if (dailyQuantity == null || dailyQuantity.signum() < 0) throw new IllegalArgumentException("dailyQuantity must be non-negative");
    }
    private static void require(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank"); }
}

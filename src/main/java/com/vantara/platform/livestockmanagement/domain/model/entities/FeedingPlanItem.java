package com.vantara.platform.livestockmanagement.domain.model.entities;

import java.math.BigDecimal;

/** An ingredient or feed quantity belonging to a feeding plan. */
public final class FeedingPlanItem {
    private final String id;
    private final String feedingPlanId;
    private final String feedName;
    private final BigDecimal dailyQuantity;
    private final String unit;

    private FeedingPlanItem(String id, String feedingPlanId, String feedName, BigDecimal dailyQuantity, String unit) {
        this.id = required(id, "Feeding plan item id");
        this.feedingPlanId = required(feedingPlanId, "Feeding plan id");
        this.feedName = required(feedName, "Feed name");
        if (dailyQuantity == null || dailyQuantity.signum() < 0) throw new IllegalArgumentException("Daily quantity must be non-negative");
        this.dailyQuantity = dailyQuantity;
        this.unit = required(unit, "Unit");
    }
    public static FeedingPlanItem create(String id, String feedingPlanId, String feedName, BigDecimal dailyQuantity, String unit) { return new FeedingPlanItem(id, feedingPlanId, feedName, dailyQuantity, unit); }
    private static String required(String value, String label) { if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " cannot be blank"); return value.trim(); }
    public String getId() { return id; } public String getFeedingPlanId() { return feedingPlanId; } public String getFeedName() { return feedName; }
    public BigDecimal getDailyQuantity() { return dailyQuantity; } public String getUnit() { return unit; }
}

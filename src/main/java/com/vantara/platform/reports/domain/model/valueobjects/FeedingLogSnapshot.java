package com.vantara.platform.reports.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Read model of a feeding record, as published by the livestock context.
 *
 * @param planItemId identifier of the plan item that was fed
 * @param lotId      lot that was fed
 * @param feedName   name of the feed
 * @param feedAt     date and time of the feeding
 * @param quantity   quantity delivered
 * @param unit       unit of the quantity
 */
public record FeedingLogSnapshot(
        String planItemId,
        String lotId,
        String feedName,
        LocalDateTime feedAt,
        BigDecimal quantity,
        String unit) {

    public FeedingLogSnapshot {
        Objects.requireNonNull(planItemId, "Plan item id cannot be null");
        Objects.requireNonNull(lotId, "Lot id cannot be null");
        Objects.requireNonNull(feedName, "Feed name cannot be null");
        Objects.requireNonNull(feedAt, "Feed date cannot be null");
        Objects.requireNonNull(quantity, "Quantity cannot be null");
        Objects.requireNonNull(unit, "Unit cannot be null");
    }
}

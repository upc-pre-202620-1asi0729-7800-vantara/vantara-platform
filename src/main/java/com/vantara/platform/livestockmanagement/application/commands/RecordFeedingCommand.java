package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Request to record a completed feeding delivery. */
public record RecordFeedingCommand(String planItemId, String animalId, String lotId, String feedName,
                                   LocalDateTime feedAt, BigDecimal quantity, String unit, String recordedBy, String notes) {
    public RecordFeedingCommand {
        require(planItemId, "planItemId"); require(lotId, "lotId"); require(feedName, "feedName"); require(unit, "unit"); require(recordedBy, "recordedBy");
        if (feedAt == null) throw new IllegalArgumentException("feedAt cannot be null");
        if (quantity == null || quantity.signum() < 0) throw new IllegalArgumentException("quantity must be non-negative");
    }
    private static void require(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank"); }
}

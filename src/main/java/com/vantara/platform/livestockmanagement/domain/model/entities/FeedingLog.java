package com.vantara.platform.livestockmanagement.domain.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Immutable proof that a feed quantity was delivered. */
public final class FeedingLog {
    private final String id;
    private final String planItemId;
    private final String animalId;
    private final String lotId;
    private final String feedName;
    private final LocalDateTime feedAt;
    private final BigDecimal quantity;
    private final String unit;
    private final String recordedBy;
    private final String notes;
    private FeedingLog(String id, String planItemId, String animalId, String lotId, String feedName, LocalDateTime feedAt,
                       BigDecimal quantity, String unit, String recordedBy, String notes) {
        this.id = required(id, "Feeding log id"); this.planItemId = required(planItemId, "Plan item id");
        this.animalId = animalId == null || animalId.isBlank() ? null : animalId.trim(); this.lotId = required(lotId, "Lot id");
        this.feedName = required(feedName, "Feed name"); this.feedAt = Objects.requireNonNull(feedAt, "Feed date cannot be null");
        if (quantity == null || quantity.signum() < 0) throw new IllegalArgumentException("Quantity must be non-negative");
        this.quantity = quantity; this.unit = required(unit, "Unit"); this.recordedBy = required(recordedBy, "Recorded by"); this.notes = notes == null ? "" : notes;
    }
    public static FeedingLog record(String id, String planItemId, String animalId, String lotId, String feedName,
                                    LocalDateTime feedAt, BigDecimal quantity, String unit, String recordedBy, String notes) { return new FeedingLog(id, planItemId, animalId, lotId, feedName, feedAt, quantity, unit, recordedBy, notes); }
    private static String required(String value, String label) { if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " cannot be blank"); return value.trim(); }
    public String getId() { return id; } public String getPlanItemId() { return planItemId; } public String getAnimalId() { return animalId; } public String getLotId() { return lotId; }
    public String getFeedName() { return feedName; } public LocalDateTime getFeedAt() { return feedAt; } public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; } public String getRecordedBy() { return recordedBy; } public String getNotes() { return notes; }
}

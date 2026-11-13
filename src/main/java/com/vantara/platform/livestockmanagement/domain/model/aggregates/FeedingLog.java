
package com.vantara.platform.livestockmanagement.domain.model.aggregates;

import com.vantara.platform.livestockmanagement.domain.model.events.FeedingRecordedEvent;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an immutable record of a completed feeding activity.
 */
public final class FeedingLog extends AbstractDomainAggregateRoot<FeedingLog> {

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

    private FeedingLog(
            String id,
            String planItemId,
            String animalId,
            String lotId,
            String feedName,
            LocalDateTime feedAt,
            BigDecimal quantity,
            String unit,
            String recordedBy,
            String notes
    ) {
        this.id = required(id, "Feeding log id");
        this.planItemId = required(planItemId, "Plan item id");
        this.animalId = optionalText(animalId);
        this.lotId = required(lotId, "Lot id");
        this.feedName = required(feedName, "Feed name");

        if (feedAt == null) {
            throw new IllegalArgumentException("Feed date cannot be null");
        }
        this.feedAt = feedAt;

        if (quantity == null || quantity.signum() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        this.quantity = quantity;

        this.unit = required(unit, "Unit");
        this.recordedBy = required(recordedBy, "Recorded by");
        this.notes = notes == null ? "" : notes.trim();
    }

    /**
     * Records a completed feeding and raises {@link FeedingRecordedEvent}.
     *
     * @param occurredAt instant at which the feeding is recorded
     */
    public static FeedingLog record(
            String id,
            String planItemId,
            String animalId,
            String lotId,
            String feedName,
            LocalDateTime feedAt,
            BigDecimal quantity,
            String unit,
            String recordedBy,
            String notes,
            Instant occurredAt
    ) {
        var log = reconstitute(
                id, planItemId, animalId, lotId, feedName,
                feedAt, quantity, unit, recordedBy, notes
        );

        log.registerDomainEvent(new FeedingRecordedEvent(
                log.id,
                log.planItemId,
                log.lotId,
                log.animalId,
                log.feedName,
                log.quantity,
                log.unit,
                Objects.requireNonNull(occurredAt, "Occurrence instant cannot be null")
        ));

        return log;
    }

    /**
     * Rebuilds an already recorded feeding from its stored state without raising events.
     */
    public static FeedingLog reconstitute(
            String id,
            String planItemId,
            String animalId,
            String lotId,
            String feedName,
            LocalDateTime feedAt,
            BigDecimal quantity,
            String unit,
            String recordedBy,
            String notes
    ) {
        return new FeedingLog(
                id, planItemId, animalId, lotId, feedName,
                feedAt, quantity, unit, recordedBy, notes
        );
    }

    private static String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getId() { return id; }
    public String getPlanItemId() { return planItemId; }
    public String getAnimalId() { return animalId; }
    public String getLotId() { return lotId; }
    public String getFeedName() { return feedName; }
    public LocalDateTime getFeedAt() { return feedAt; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getRecordedBy() { return recordedBy; }
    public String getNotes() { return notes; }
}

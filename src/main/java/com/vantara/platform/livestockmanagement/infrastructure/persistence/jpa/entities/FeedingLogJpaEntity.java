
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "feeding_logs")
public class FeedingLogJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String planItemId;

    @Column(length = 36)
    private String animalId;

    @Column(nullable = false, length = 36)
    private String lotId;

    @Column(nullable = false, length = 128)
    private String feedName;

    @Column(nullable = false)
    private LocalDateTime feedAt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 32)
    private String unit;

    @Column(nullable = false, length = 64)
    private String recordedBy;

    @Column(length = 4000)
    private String notes;

    protected FeedingLogJpaEntity() {
    }

    public FeedingLogJpaEntity(
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
        this.id = id;
        this.planItemId = planItemId;
        this.animalId = animalId;
        this.lotId = lotId;
        this.feedName = feedName;
        this.feedAt = feedAt;
        this.quantity = quantity;
        this.unit = unit;
        this.recordedBy = recordedBy;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getPlanItemId() {
        return planItemId;
    }

    public String getAnimalId() {
        return animalId;
    }

    public String getLotId() {
        return lotId;
    }

    public String getFeedName() {
        return feedName;
    }

    public LocalDateTime getFeedAt() {
        return feedAt;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public String getNotes() {
        return notes;
    }
}

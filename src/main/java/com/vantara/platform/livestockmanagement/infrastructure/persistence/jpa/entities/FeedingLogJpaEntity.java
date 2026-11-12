package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingLog;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "feeding_logs")
public class FeedingLogJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 36) private String planItemId;
    @Column(length = 36) private String animalId;
    @Column(nullable = false, length = 36) private String lotId;
    @Column(nullable = false, length = 128) private String feedName;
    @Column(nullable = false) private LocalDateTime feedAt;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal quantity;
    @Column(nullable = false, length = 32) private String unit;
    @Column(nullable = false, length = 64) private String recordedBy;
    @Column(length = 4000) private String notes;
    protected FeedingLogJpaEntity() { }
    private FeedingLogJpaEntity(FeedingLog log) { id=log.getId(); planItemId=log.getPlanItemId(); animalId=log.getAnimalId(); lotId=log.getLotId(); feedName=log.getFeedName(); feedAt=log.getFeedAt(); quantity=log.getQuantity(); unit=log.getUnit(); recordedBy=log.getRecordedBy(); notes=log.getNotes(); }
    public static FeedingLogJpaEntity fromDomain(FeedingLog log) { return new FeedingLogJpaEntity(log); }
    public FeedingLog toDomain() { return FeedingLog.record(id, planItemId, animalId, lotId, feedName, feedAt, quantity, unit, recordedBy, notes); }
}

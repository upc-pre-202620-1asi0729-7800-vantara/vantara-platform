
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
public class MovementJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String animalId;

    @Column(length = 36)
    private String originLotId;

    @Column(nullable = false, length = 36)
    private String destinationLotId;

    @Column(nullable = false, length = 32)
    private String movementType;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false, length = 1024)
    private String reason;

    @Column(nullable = false, length = 64)
    private String recordedBy;

    protected MovementJpaEntity() {
    }

    public MovementJpaEntity(
            String id,
            String animalId,
            String originLotId,
            String destinationLotId,
            String movementType,
            LocalDateTime occurredAt,
            String reason,
            String recordedBy
    ) {
        this.id = id;
        this.animalId = animalId;
        this.originLotId = originLotId;
        this.destinationLotId = destinationLotId;
        this.movementType = movementType;
        this.occurredAt = occurredAt;
        this.reason = reason;
        this.recordedBy = recordedBy;
    }

    public String getId() {
        return id;
    }

    public String getAnimalId() {
        return animalId;
    }

    public String getOriginLotId() {
        return originLotId;
    }

    public String getDestinationLotId() {
        return destinationLotId;
    }

    public String getMovementType() {
        return movementType;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getReason() {
        return reason;
    }

    public String getRecordedBy() {
        return recordedBy;
    }
}

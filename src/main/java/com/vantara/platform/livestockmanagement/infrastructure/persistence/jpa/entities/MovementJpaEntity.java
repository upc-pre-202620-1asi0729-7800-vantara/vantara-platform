package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
public class MovementJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 36) private String animalId;
    @Column(length = 36) private String originLotId;
    @Column(nullable = false, length = 36) private String destinationLotId;
    @Column(nullable = false, length = 32) private String movementType;
    @Column(nullable = false) private LocalDateTime occurredAt;
    @Column(nullable = false, length = 1024) private String reason;
    @Column(nullable = false, length = 64) private String recordedBy;
    protected MovementJpaEntity() { }
    private MovementJpaEntity(Movement movement) { id=movement.getId(); animalId=movement.getAnimalId(); originLotId=movement.getOriginLotId(); destinationLotId=movement.getDestinationLotId(); movementType=movement.getMovementType(); occurredAt=movement.getOccurredAt(); reason=movement.getReason(); recordedBy=movement.getRecordedBy(); }
    public static MovementJpaEntity fromDomain(Movement movement) { return new MovementJpaEntity(movement); }
    public Movement toDomain() { return Movement.record(id, animalId, originLotId, destinationLotId, movementType, occurredAt, reason, recordedBy); }
}

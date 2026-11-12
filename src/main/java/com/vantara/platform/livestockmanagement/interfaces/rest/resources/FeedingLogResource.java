package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingLog;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FeedingLogResource(String id, @NotBlank String planItemId, String animalId, @NotBlank String lotId,
        @NotBlank String feedName, @NotNull LocalDateTime feedAt, @NotNull @DecimalMin("0") BigDecimal quantity,
        @NotBlank String unit, @NotBlank String recordedBy, String notes) {
    public FeedingLog toDomain(String generatedId) { return FeedingLog.record(id == null || id.isBlank() ? generatedId : id, planItemId, animalId, lotId, feedName, feedAt, quantity, unit, recordedBy, notes); }
    public static FeedingLogResource from(FeedingLog log) { return new FeedingLogResource(log.getId(), log.getPlanItemId(), log.getAnimalId(), log.getLotId(), log.getFeedName(), log.getFeedAt(), log.getQuantity(), log.getUnit(), log.getRecordedBy(), log.getNotes()); }
}

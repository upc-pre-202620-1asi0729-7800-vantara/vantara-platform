
package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FeedingLogResource(
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

    public static FeedingLogResource from(FeedingLog log) {
        return new FeedingLogResource(
                log.getId(),
                log.getPlanItemId(),
                log.getAnimalId(),
                log.getLotId(),
                log.getFeedName(),
                log.getFeedAt(),
                log.getQuantity(),
                log.getUnit(),
                log.getRecordedBy(),
                log.getNotes()
        );
    }
}

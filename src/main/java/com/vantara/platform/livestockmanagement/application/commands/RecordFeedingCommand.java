
package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request to record a completed feeding delivery.
 */
public record RecordFeedingCommand(
        String feedingPlanId,
        String planItemId,
        String animalId,
        String lotId,
        LocalDateTime feedAt,
        BigDecimal quantity,
        String recordedBy,
        String notes
) {

    public RecordFeedingCommand {
        require(feedingPlanId, "feedingPlanId");
        require(planItemId, "planItemId");
        require(lotId, "lotId");
        require(recordedBy, "recordedBy");

        // Normalize optional animalId.
        animalId = animalId == null || animalId.isBlank()
                ? null
                : animalId.trim();

        if (feedAt == null) {
            throw new IllegalArgumentException(
                    "feedAt cannot be null"
            );
        }

        if (quantity == null || quantity.signum() < 0) {
            throw new IllegalArgumentException(
                    "quantity must be non-negative"
            );
        }

        feedingPlanId = feedingPlanId.trim();
        planItemId = planItemId.trim();
        lotId = lotId.trim();
        recordedBy = recordedBy.trim();

        notes = notes == null ? "" : notes.trim();
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }
}

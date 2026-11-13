package com.vantara.platform.livestockmanagement.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Raised when a completed feeding activity is recorded ("feeding recorded").
 *
 * @param feedingLogId identifier of the feeding log
 * @param planItemId   plan item that was fed
 * @param lotId        lot that was fed
 * @param animalId     animal that was fed, or {@code null} for a lot-wide feeding
 * @param feedName     feed given
 * @param quantity     quantity given
 * @param unit         unit of the quantity
 * @param occurredAt   instant at which the feeding was recorded
 */
public record FeedingRecordedEvent(
        String feedingLogId,
        String planItemId,
        String lotId,
        String animalId,
        String feedName,
        BigDecimal quantity,
        String unit,
        Instant occurredAt
) implements DomainEvent {
}

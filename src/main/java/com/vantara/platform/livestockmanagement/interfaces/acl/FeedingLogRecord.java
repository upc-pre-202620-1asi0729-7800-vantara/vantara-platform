package com.vantara.platform.livestockmanagement.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read view of a feeding record published to other bounded contexts.
 *
 * @param planItemId identifier of the plan item that was fed
 * @param lotId      lot that was fed
 * @param feedName   name of the feed
 * @param feedAt     date and time of the feeding
 * @param quantity   quantity delivered
 * @param unit       unit of the quantity
 */
public record FeedingLogRecord(
        String planItemId,
        String lotId,
        String feedName,
        LocalDateTime feedAt,
        BigDecimal quantity,
        String unit) {
}

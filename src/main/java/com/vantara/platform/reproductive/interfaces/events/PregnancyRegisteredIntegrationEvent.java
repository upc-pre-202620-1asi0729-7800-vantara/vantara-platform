package com.vantara.platform.reproductive.interfaces.events;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published by the {@code reproductive} context when a pregnancy is registered.
 *
 * <p>This is part of the published language of the context: other bounded contexts (for example notifications) must
 * listen to this event rather than to internal domain events.</p>
 *
 * @param recordId          identifier of the reproductive record
 * @param animalId          identifier of the pregnant animal
 * @param confirmedOn       date the pregnancy was confirmed
 * @param expectedCalvingOn expected calving date, or {@code null}
 * @param occurredAt        instant at which the registration happened
 */
public record PregnancyRegisteredIntegrationEvent(
        Long recordId,
        Long animalId,
        LocalDate confirmedOn,
        LocalDate expectedCalvingOn,
        Instant occurredAt
) {
}

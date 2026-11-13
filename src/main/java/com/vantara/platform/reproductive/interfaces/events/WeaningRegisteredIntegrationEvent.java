package com.vantara.platform.reproductive.interfaces.events;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Integration event published by the {@code reproductive} context when a weaning is registered.
 *
 * @param recordId   identifier of the reproductive record
 * @param animalId   identifier of the mother
 * @param calfId     identifier of the weaned calf
 * @param weanedOn   date of the weaning
 * @param occurredAt instant at which the registration happened
 */
public record WeaningRegisteredIntegrationEvent(
        Long recordId,
        String animalId,
        String calfId,
        LocalDate weanedOn,
        Instant occurredAt
) {
}

package com.vantara.platform.livestockmanagement.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Read view of an animal published to other bounded contexts. It exposes no livestock internals.
 *
 * @param id           identifier of the animal
 * @param rancherId    owner of the animal
 * @param lotId        lot where the animal is kept
 * @param breed        breed
 * @param sex          sex as registered
 * @param status       status of the animal
 * @param weight       current weight in kilograms
 * @param registeredAt registration date
 */
public record AnimalRecord(
        String id,
        String rancherId,
        String lotId,
        String breed,
        String sex,
        String status,
        BigDecimal weight,
        LocalDate registeredAt) {
}

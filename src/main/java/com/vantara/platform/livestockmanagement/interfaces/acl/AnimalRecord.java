
package com.vantara.platform.livestockmanagement.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Read view of an animal published to other bounded contexts.
 * It exposes no livestock internals.
 */
public record AnimalRecord(
        String id,
        String rancherId,
        String lotId,
        String earTag,
        String name,
        String species,
        String breed,
        String sex,
        LocalDate birthDate,
        String motherId,
        String fatherId,
        String status,
        BigDecimal weight,
        LocalDate registeredAt,
        String photoUrl,
        LocalDate weanedOn
) {
}

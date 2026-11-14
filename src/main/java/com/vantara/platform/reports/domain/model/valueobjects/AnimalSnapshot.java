package com.vantara.platform.reports.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Read model of an animal, as published by the livestock context. Reports never own or change it.
 *
 * @param id           identifier of the animal
 * @param rancherId    owner of the animal
 * @param lotId        lot where the animal is kept
 * @param breed        breed
 * @param sex          sex as registered (for example {@code Macho} or {@code Hembra})
 * @param status       status of the animal
 * @param weight       current weight in kilograms
 * @param registeredAt date the animal was registered
 */
public record AnimalSnapshot(
        String id,
        String rancherId,
        String lotId,
        String breed,
        String sex,
        String status,
        BigDecimal weight,
        LocalDate registeredAt) {

    public AnimalSnapshot {
        Objects.requireNonNull(id, "Animal id cannot be null");
        Objects.requireNonNull(rancherId, "Rancher id cannot be null");
        Objects.requireNonNull(lotId, "Lot id cannot be null");
        Objects.requireNonNull(breed, "Breed cannot be null");
        Objects.requireNonNull(sex, "Sex cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(weight, "Weight cannot be null");
        Objects.requireNonNull(registeredAt, "Registration date cannot be null");
    }
}

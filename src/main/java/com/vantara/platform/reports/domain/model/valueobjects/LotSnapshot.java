package com.vantara.platform.reports.domain.model.valueobjects;

import java.util.Objects;

/**
 * Read model of a lot, as published by the livestock context.
 *
 * @param id        identifier of the lot
 * @param rancherId owner of the lot
 * @param name      name of the lot
 */
public record LotSnapshot(String id, String rancherId, String name) {

    public LotSnapshot {
        Objects.requireNonNull(id, "Lot id cannot be null");
        Objects.requireNonNull(rancherId, "Rancher id cannot be null");
        Objects.requireNonNull(name, "Lot name cannot be null");
    }
}

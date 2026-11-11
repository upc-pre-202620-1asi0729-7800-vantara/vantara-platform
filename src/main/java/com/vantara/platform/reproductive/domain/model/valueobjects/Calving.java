package com.vantara.platform.reproductive.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data of the calving that ended a pregnancy.
 *
 * @param calvedOn    date of the calving
 * @param outcome     result of the calving
 * @param calfId      identifier of the calf in livestock, or {@code null} when no calf was registered
 * @param birthWeight birth weight of the calf, present only when {@code calfId} is present
 */
public record Calving(LocalDate calvedOn, CalvingOutcome outcome, AnimalId calfId, WeightInKg birthWeight) {
    public Calving {
        Objects.requireNonNull(calvedOn, "Calving date cannot be null");
        Objects.requireNonNull(outcome, "Calving outcome cannot be null");
        if (calfId == null && birthWeight != null) {
            throw new IllegalArgumentException("A birth weight requires a registered calf");
        }
        if (calfId != null && birthWeight == null) {
            throw new IllegalArgumentException("A registered calf requires its birth weight");
        }
    }

    /**
     * @return {@code true} when a calf was registered with this calving
     */
    public boolean hasCalf() {
        return calfId != null;
    }
}

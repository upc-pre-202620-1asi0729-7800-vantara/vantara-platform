package com.vantara.platform.reproductive.domain.model.valueobjects;

/**
 * Lifecycle status of a reproductive record.
 *
 * <p>Allowed transitions: {@code CONFIRMED -> DRY_OFF -> ENDED -> WEANED}, where {@code DRY_OFF} is optional.</p>
 */
public enum ReproductiveStatus {
    /** The pregnancy was confirmed and is in progress. */
    CONFIRMED,
    /** The dry-off was registered and the pregnancy is still in progress. */
    DRY_OFF,
    /** The pregnancy ended with a registered calving. */
    ENDED,
    /** The calf of the calving was weaned. */
    WEANED;

    /**
     * @return {@code true} while the pregnancy has not ended in a calving
     */
    public boolean isPregnancyInProgress() {
        return this == CONFIRMED || this == DRY_OFF;
    }
}

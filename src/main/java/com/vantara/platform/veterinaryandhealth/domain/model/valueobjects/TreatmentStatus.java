package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Lifecycle of a prescribed treatment. Constants mirror the values exposed by the API and stored in the database.
 */
public enum TreatmentStatus {
    active,
    completed,
    cancelled
}

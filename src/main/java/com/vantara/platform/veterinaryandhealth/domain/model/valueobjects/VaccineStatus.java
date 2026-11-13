package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Application state of a vaccine dose. Constants mirror the values exposed by the API and stored in the database.
 */
public enum VaccineStatus {
    completed,
    pending
}

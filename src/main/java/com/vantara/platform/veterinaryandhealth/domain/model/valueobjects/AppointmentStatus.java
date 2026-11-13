package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Lifecycle of an appointment. Constants mirror the values exposed by the API and stored in the database.
 */
public enum AppointmentStatus {
    scheduled,
    completed,
    cancelled
}

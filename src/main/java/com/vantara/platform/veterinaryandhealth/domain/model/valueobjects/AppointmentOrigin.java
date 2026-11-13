package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Origin of an appointment. Constants mirror the values exposed by the API and stored in the database.
 */
public enum AppointmentOrigin {
    RANCHER_REQUEST,
    TECHNICAL_VISIT
}

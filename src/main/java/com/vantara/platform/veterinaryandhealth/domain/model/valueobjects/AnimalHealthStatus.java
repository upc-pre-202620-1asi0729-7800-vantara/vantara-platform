package com.vantara.platform.veterinaryandhealth.domain.model.valueobjects;

/**
 * Health status of an animal after a clinical record. Constants mirror the values exposed by the API and stored in the database.
 */
public enum AnimalHealthStatus {
    healthy,
    follow_up,
    critical
}

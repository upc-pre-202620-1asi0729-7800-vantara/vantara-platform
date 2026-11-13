package com.vantara.platform.veterinaryandhealth.application.commandservices;

/**
 * Expected business failures returned by the veterinary and health command operations.
 */
public sealed interface VeterinaryAndHealthCommandFailure
        permits VeterinaryAndHealthCommandFailure.AppointmentNotFound,
        VeterinaryAndHealthCommandFailure.MedicalRecordNotFound,
        VeterinaryAndHealthCommandFailure.MedicationNotFound,
        VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus {

    /**
     * The referenced appointment does not exist.
     */
    record AppointmentNotFound(String appointmentId) implements VeterinaryAndHealthCommandFailure {
    }

    /**
     * The referenced clinical record does not exist.
     */
    record MedicalRecordNotFound(String medicalRecordId) implements VeterinaryAndHealthCommandFailure {
    }

    /**
     * The referenced medication does not exist in the catalog.
     */
    record MedicationNotFound(String medicationId) implements VeterinaryAndHealthCommandFailure {
    }

    /**
     * The requested change is not allowed for the current appointment status.
     */
    record InvalidAppointmentStatus(String detail) implements VeterinaryAndHealthCommandFailure {
    }
}

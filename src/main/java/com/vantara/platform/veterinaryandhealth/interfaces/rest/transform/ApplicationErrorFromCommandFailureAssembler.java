package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandFailure;

/**
 * Translates an expected command failure into the shared {@link ApplicationError}, so the shared
 * error assembler produces the HTTP status and localized message.
 */
public final class ApplicationErrorFromCommandFailureAssembler {

    private ApplicationErrorFromCommandFailureAssembler() {
    }

    public static ApplicationError toApplicationError(VeterinaryAndHealthCommandFailure failure) {
        return switch (failure) {
            case VeterinaryAndHealthCommandFailure.AppointmentNotFound notFound ->
                    ApplicationError.notFound("appointment", notFound.appointmentId());
            case VeterinaryAndHealthCommandFailure.MedicalRecordNotFound recordNotFound ->
                    ApplicationError.notFound("medical-record", recordNotFound.medicalRecordId());
            case VeterinaryAndHealthCommandFailure.MedicationNotFound medicationNotFound ->
                    ApplicationError.notFound("medication", medicationNotFound.medicationId());
            case VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus invalid ->
                    ApplicationError.businessRuleViolation("appointment-status", invalid.detail());
        };
    }
}

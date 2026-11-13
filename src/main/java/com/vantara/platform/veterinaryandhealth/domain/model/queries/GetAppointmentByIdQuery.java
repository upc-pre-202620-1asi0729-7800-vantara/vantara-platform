package com.vantara.platform.veterinaryandhealth.domain.model.queries;

/**
 * Query to retrieve a veterinary appointment by its identifier.
 *
 * @param id identifier of the appointment
 */
public record GetAppointmentByIdQuery(String id) {

    public GetAppointmentByIdQuery {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }
    }
}

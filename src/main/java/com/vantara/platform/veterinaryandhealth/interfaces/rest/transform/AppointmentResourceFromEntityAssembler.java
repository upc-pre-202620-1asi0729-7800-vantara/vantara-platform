package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentResource;

/**
 * Translates the {@link Appointment} aggregate into its HTTP resource.
 */
public final class AppointmentResourceFromEntityAssembler {

    private AppointmentResourceFromEntityAssembler() {
    }

    public static AppointmentResource toResourceFromEntity(Appointment appointment) {
        return new AppointmentResource(
                appointment.getId(),
                appointment.getRancherId().value(),
                appointment.getVeterinarianId().value(),
                appointment.getRequestedBy().value(),
                appointment.getOrigin().name(),
                appointment.getScheduledAt(),
                appointment.getReason(),
                appointment.getStatus().name(),
                appointment.getCancellationReason(),
                appointment.getCreatedAt());
    }
}

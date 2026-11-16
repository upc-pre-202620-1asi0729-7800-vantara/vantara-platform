
package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.UpdateAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.UpdateAppointmentResource;

import java.util.Locale;

/**
 * Translates a partial appointment update into an UpdateAppointmentCommand.
 */
public final class UpdateAppointmentCommandFromResourceAssembler {

    private UpdateAppointmentCommandFromResourceAssembler() {
    }

    public static UpdateAppointmentCommand toCommandFromResource(
            String appointmentId,
            UpdateAppointmentResource resource,
            Long actorId) {

        return new UpdateAppointmentCommand(
                appointmentId,
                actorId,
                toStatusOrNull(resource.status()),
                resource.cancellationReason(),
                resource.scheduledAt(),
                resource.reason()
        );
    }

    private static AppointmentStatus toStatusOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return AppointmentStatus.valueOf(
                value.trim().toLowerCase(Locale.ROOT)
        );
    }
}

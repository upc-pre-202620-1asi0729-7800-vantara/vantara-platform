package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.CreateAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentOrigin;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.CreateAppointmentResource;

import java.util.Locale;

/**
 * Translates a create-appointment request into a {@link CreateAppointmentCommand}.
 */
public final class CreateAppointmentCommandFromResourceAssembler {

    private CreateAppointmentCommandFromResourceAssembler() {
    }

    public static CreateAppointmentCommand toCommandFromResource(CreateAppointmentResource resource) {
        return new CreateAppointmentCommand(
                new UserId(resource.rancherId()),
                new UserId(resource.veterinarianId()),
                new UserId(resource.requestedBy()),
                AppointmentOrigin.valueOf(resource.origin().trim().toUpperCase(Locale.ROOT)),
                resource.scheduledAt(),
                resource.reason());
    }
}

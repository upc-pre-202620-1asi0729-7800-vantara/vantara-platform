
package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.CreateAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentOrigin;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.CreateAppointmentResource;

import java.util.Locale;

public final class CreateAppointmentCommandFromResourceAssembler {

    private CreateAppointmentCommandFromResourceAssembler() {
    }

    public static CreateAppointmentCommand toCommandFromResource(
            CreateAppointmentResource resource,
            Long rancherId,
            Long veterinarianId,
            Long requestedBy) {

        return new CreateAppointmentCommand(
                new UserId(rancherId),
                new UserId(veterinarianId),
                new UserId(requestedBy),
                AppointmentOrigin.valueOf(
                        resource.origin().trim().toUpperCase(Locale.ROOT)
                ),
                resource.scheduledAt(),
                resource.reason()
        );
    }
}

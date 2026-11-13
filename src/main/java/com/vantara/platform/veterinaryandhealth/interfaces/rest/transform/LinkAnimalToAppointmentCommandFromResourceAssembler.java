package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.LinkAnimalToAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentAnimalResource;

/**
 * Translates a link request into a {@link LinkAnimalToAppointmentCommand}.
 */
public final class LinkAnimalToAppointmentCommandFromResourceAssembler {

    private LinkAnimalToAppointmentCommandFromResourceAssembler() {
    }

    public static LinkAnimalToAppointmentCommand toCommandFromResource(AppointmentAnimalResource resource) {
        return new LinkAnimalToAppointmentCommand(resource.appointmentId(), new AnimalId(resource.animalId()));
    }
}

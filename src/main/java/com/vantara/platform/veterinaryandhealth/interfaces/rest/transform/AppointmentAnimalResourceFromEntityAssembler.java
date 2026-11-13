package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentAnimalResource;

/**
 * Translates the {@link AppointmentAnimal} aggregate into its HTTP resource.
 */
public final class AppointmentAnimalResourceFromEntityAssembler {

    private AppointmentAnimalResourceFromEntityAssembler() {
    }

    public static AppointmentAnimalResource toResourceFromEntity(AppointmentAnimal appointmentAnimal) {
        return new AppointmentAnimalResource(
                appointmentAnimal.getId(),
                appointmentAnimal.getAppointmentId(),
                appointmentAnimal.getAnimalId().value());
    }
}

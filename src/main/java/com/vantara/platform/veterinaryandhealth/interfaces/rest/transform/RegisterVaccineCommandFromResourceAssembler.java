
package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.RegisterVaccineCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.VaccineStatus;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.RegisterVaccineResource;

import java.util.Locale;

public final class RegisterVaccineCommandFromResourceAssembler {

    private RegisterVaccineCommandFromResourceAssembler() {
    }

    public static RegisterVaccineCommand toCommandFromResource(
            RegisterVaccineResource resource,
            Long actorId) {

        return new RegisterVaccineCommand(
                actorId,
                resource.appointmentId(),
                new AnimalId(resource.animalId()),
                new UserId(resource.veterinarianId()),
                resource.vaccineType(),
                resource.name(),
                new Dose(resource.dose(), resource.doseUnit()),
                resource.laboratory(),
                resource.lotNumber(),
                resource.appliedAt(),
                resource.nextDoseAt(),
                resource.route(),
                resource.applicationSite(),
                VaccineStatus.valueOf(
                        resource.status().trim().toLowerCase(Locale.ROOT)),
                resource.observations()
        );
    }
}

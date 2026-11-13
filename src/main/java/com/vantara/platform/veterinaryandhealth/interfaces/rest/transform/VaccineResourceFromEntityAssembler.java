package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VaccineResource;

/**
 * Translates the {@link Vaccine} aggregate into its HTTP resource.
 */
public final class VaccineResourceFromEntityAssembler {

    private VaccineResourceFromEntityAssembler() {
    }

    public static VaccineResource toResourceFromEntity(Vaccine vaccine) {
        return new VaccineResource(
                vaccine.getId(),
                vaccine.getAnimalId().value(),
                vaccine.getVeterinarianId().value(),
                vaccine.getVaccineType(),
                vaccine.getName(),
                vaccine.getDose().amount(),
                vaccine.getDose().unit(),
                vaccine.getLaboratory(),
                vaccine.getLotNumber(),
                vaccine.getAppliedAt(),
                vaccine.getNextDoseAt(),
                vaccine.getRoute(),
                vaccine.getApplicationSite(),
                vaccine.getStatus().name(),
                vaccine.getObservations());
    }
}

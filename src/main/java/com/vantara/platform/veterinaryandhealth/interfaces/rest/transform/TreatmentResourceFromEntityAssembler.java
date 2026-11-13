package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.TreatmentResource;

/**
 * Translates the {@link Treatment} aggregate into its HTTP resource.
 */
public final class TreatmentResourceFromEntityAssembler {

    private TreatmentResourceFromEntityAssembler() {
    }

    public static TreatmentResource toResourceFromEntity(Treatment treatment) {
        return new TreatmentResource(
                treatment.getId(),
                treatment.getMedicalRecordId(),
                treatment.getAnimalId().value(),
                treatment.getMedicationId(),
                treatment.getTreatmentType(),
                treatment.getDose().amount(),
                treatment.getDose().unit(),
                treatment.getRoute(),
                treatment.getFrequencyHours(),
                treatment.getDurationDays(),
                treatment.getScheduledAt(),
                treatment.getAdministeredAt(),
                treatment.getNextDate(),
                treatment.getStatus().name(),
                treatment.getInstructions());
    }
}

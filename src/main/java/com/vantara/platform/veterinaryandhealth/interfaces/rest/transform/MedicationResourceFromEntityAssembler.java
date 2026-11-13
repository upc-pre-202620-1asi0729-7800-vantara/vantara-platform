package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Medication;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.MedicationResource;

/**
 * Translates the {@link Medication} catalog entry into its HTTP resource.
 */
public final class MedicationResourceFromEntityAssembler {

    private MedicationResourceFromEntityAssembler() {
    }

    public static MedicationResource toResourceFromEntity(Medication medication) {
        return new MedicationResource(
                medication.getId(),
                medication.getName(),
                medication.getCategory(),
                medication.getActiveIngredient(),
                medication.getPresentation());
    }
}

package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Medication;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.MedicationPersistenceEntity;

/**
 * Translates the {@link Medication} catalog entry from its persistence entity. The catalog is read-only,
 * so there is no reverse translation.
 */
public final class MedicationPersistenceAssembler {

    private MedicationPersistenceAssembler() {
    }

    public static Medication toDomainFromPersistence(MedicationPersistenceEntity entity) {
        return Medication.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getActiveIngredient(),
                entity.getPresentation());
    }
}

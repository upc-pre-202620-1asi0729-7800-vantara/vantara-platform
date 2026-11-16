
package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.PrescribeTreatmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.PrescribeTreatmentResource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class PrescribeTreatmentCommandFromResourceAssembler {

    private PrescribeTreatmentCommandFromResourceAssembler() {
    }

    public static PrescribeTreatmentCommand toCommandFromResource(
            PrescribeTreatmentResource resource,
            Long actorId) {

        return new PrescribeTreatmentCommand(
                actorId,
                resource.medicalRecordId(),
                new AnimalId(resource.animalId()),
                resource.medicationId(),
                resource.treatmentType(),
                new Dose(resource.dose(), resource.doseUnit()),
                resource.route(),
                resource.frequencyHours(),
                resource.durationDays(),
                toUtcDateTime(resource.scheduledAt()),
                toUtcDateTime(resource.administeredAt()),
                resource.nextDate(),
                resource.instructions()
        );
    }

    private static LocalDateTime toUtcDateTime(Instant instant) {
        return instant == null
                ? null
                : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}

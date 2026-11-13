package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.commands.RegisterMedicalRecordCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalHealthStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.MedicalRecordSeverity;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.RegisterMedicalRecordResource;

import java.util.Locale;

/**
 * Translates a clinical record request into a {@link RegisterMedicalRecordCommand}.
 */
public final class RegisterMedicalRecordCommandFromResourceAssembler {

    private RegisterMedicalRecordCommandFromResourceAssembler() {
    }

    public static RegisterMedicalRecordCommand toCommandFromResource(RegisterMedicalRecordResource resource) {
        return new RegisterMedicalRecordCommand(
                resource.appointmentId(),
                new AnimalId(resource.animalId()),
                new UserId(resource.veterinarianId()),
                resource.recordedAt(),
                resource.symptoms(),
                resource.diagnosis(),
                MedicalRecordSeverity.valueOf(resource.severity().trim().toLowerCase(Locale.ROOT)),
                AnimalHealthStatus.valueOf(resource.healthStatus().trim().toLowerCase(Locale.ROOT)),
                resource.notes());
    }
}

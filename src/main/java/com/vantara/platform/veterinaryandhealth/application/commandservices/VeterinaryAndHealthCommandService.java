package com.vantara.platform.veterinaryandhealth.application.commandservices;

import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.CreateAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.LinkAnimalToAppointmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.PrescribeTreatmentCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.RegisterMedicalRecordCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.RegisterVaccineCommand;
import com.vantara.platform.veterinaryandhealth.domain.model.commands.UpdateAppointmentCommand;

/**
 * Write-side contract of the veterinary and health use cases. Expected business failures
 * are returned as {@link VeterinaryAndHealthCommandFailure} inside a {@link Result}.
 */
public interface VeterinaryAndHealthCommandService {

    Result<Appointment, VeterinaryAndHealthCommandFailure> handle(CreateAppointmentCommand command);

    Result<Appointment, VeterinaryAndHealthCommandFailure> handle(UpdateAppointmentCommand command);

    Result<AppointmentAnimal, VeterinaryAndHealthCommandFailure> handle(LinkAnimalToAppointmentCommand command);

    Result<MedicalRecord, VeterinaryAndHealthCommandFailure> handle(RegisterMedicalRecordCommand command);

    Result<Treatment, VeterinaryAndHealthCommandFailure> handle(PrescribeTreatmentCommand command);

    Result<Vaccine, VeterinaryAndHealthCommandFailure> handle(RegisterVaccineCommand command);
}

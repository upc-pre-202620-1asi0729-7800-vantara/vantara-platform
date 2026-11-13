package com.vantara.platform.veterinaryandhealth.application.internal.commandservices;

import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandFailure;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
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
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentAnimalRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicalRecordRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicationRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.TreatmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.VaccineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@Transactional
public class VeterinaryAndHealthCommandServiceImpl implements VeterinaryAndHealthCommandService {

    private final AppointmentRepository appointments;
    private final AppointmentAnimalRepository appointmentAnimals;
    private final MedicalRecordRepository medicalRecords;
    private final MedicationRepository medications;
    private final TreatmentRepository treatments;
    private final VaccineRepository vaccines;
    private final Clock clock;

    public VeterinaryAndHealthCommandServiceImpl(
            AppointmentRepository appointments,
            AppointmentAnimalRepository appointmentAnimals,
            MedicalRecordRepository medicalRecords,
            MedicationRepository medications,
            TreatmentRepository treatments,
            VaccineRepository vaccines,
            Clock clock) {
        this.appointments = appointments;
        this.appointmentAnimals = appointmentAnimals;
        this.medicalRecords = medicalRecords;
        this.medications = medications;
        this.treatments = treatments;
        this.vaccines = vaccines;
        this.clock = clock;
    }

    @Override
    public Result<Appointment, VeterinaryAndHealthCommandFailure> handle(CreateAppointmentCommand command) {
        var appointment = Appointment.schedule(id(), command.rancherId(), command.veterinarianId(),
                command.requestedBy(), command.origin(), command.scheduledAt(), command.reason(), clock.instant());
        return Result.success(appointments.save(appointment));
    }

    @Override
    public Result<Appointment, VeterinaryAndHealthCommandFailure> handle(UpdateAppointmentCommand command) {
        var found = appointments.findById(command.appointmentId());
        if (found.isEmpty()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.AppointmentNotFound(command.appointmentId()));
        }
        var appointment = found.orElseThrow();
        if (command.status() != null && !appointment.canChangeTo(command.status())) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus(
                    "Only scheduled appointments can change status"));
        }
        if ((command.scheduledAt() != null || command.reason() != null) && !appointment.isScheduled()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus(
                    "Only scheduled appointments can be modified"));
        }
        if (command.scheduledAt() != null) {
            appointment.reschedule(command.scheduledAt());
        }
        if (command.reason() != null) {
            appointment.changeReason(command.reason());
        }
        if (command.status() != null) {
            appointment.changeStatus(command.status(), command.cancellationReason());
        }
        return Result.success(appointments.save(appointment));
    }

    @Override
    public Result<AppointmentAnimal, VeterinaryAndHealthCommandFailure> handle(LinkAnimalToAppointmentCommand command) {
        if (appointments.findById(command.appointmentId()).isEmpty()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.AppointmentNotFound(command.appointmentId()));
        }
        var existing = appointmentAnimals.findByAppointmentIdAndAnimalId(command.appointmentId(), command.animalId());
        if (existing.isPresent()) {
            return Result.success(existing.orElseThrow());
        }
        var link = AppointmentAnimal.link(id(), command.appointmentId(), command.animalId());
        return Result.success(appointmentAnimals.save(link));
    }

    @Override
    public Result<MedicalRecord, VeterinaryAndHealthCommandFailure> handle(RegisterMedicalRecordCommand command) {
        if (command.appointmentId() != null && appointments.findById(command.appointmentId()).isEmpty()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.AppointmentNotFound(command.appointmentId()));
        }
        var record = MedicalRecord.register(id(), command.appointmentId(), command.animalId(),
                command.veterinarianId(), command.recordedAt(), command.symptoms(), command.diagnosis(),
                command.severity(), command.healthStatus(), command.notes(), clock.instant());
        return Result.success(medicalRecords.save(record));
    }

    @Override
    public Result<Treatment, VeterinaryAndHealthCommandFailure> handle(PrescribeTreatmentCommand command) {
        if (medicalRecords.findById(command.medicalRecordId()).isEmpty()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.MedicalRecordNotFound(command.medicalRecordId()));
        }
        if (medications.findById(command.medicationId()).isEmpty()) {
            return Result.failure(new VeterinaryAndHealthCommandFailure.MedicationNotFound(command.medicationId()));
        }
        var treatment = Treatment.prescribe(id(), command.medicalRecordId(), command.animalId(),
                command.medicationId(), command.treatmentType(), command.dose(), command.route(),
                command.frequencyHours(), command.durationDays(), command.scheduledAt(),
                command.administeredAt(), command.nextDate(), command.instructions(), clock.instant());
        return Result.success(treatments.save(treatment));
    }

    @Override
    public Result<Vaccine, VeterinaryAndHealthCommandFailure> handle(RegisterVaccineCommand command) {
        var vaccine = Vaccine.register(id(), command.animalId(), command.veterinarianId(), command.vaccineType(),
                command.name(), command.dose(), command.laboratory(), command.lotNumber(), command.appliedAt(),
                command.nextDoseAt(), command.route(), command.applicationSite(), command.status(),
                command.observations(), clock.instant());
        return Result.success(vaccines.save(vaccine));
    }

    private String id() {
        return UUID.randomUUID().toString();
    }
}

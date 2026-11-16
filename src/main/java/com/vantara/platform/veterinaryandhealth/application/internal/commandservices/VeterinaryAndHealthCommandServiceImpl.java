
package com.vantara.platform.veterinaryandhealth.application.internal.commandservices;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandFailure;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.internal.authorization.VeterinaryAuthorizationService;
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
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentAnimalRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicalRecordRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicationRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.TreatmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.VaccineRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class VeterinaryAndHealthCommandServiceImpl
        implements VeterinaryAndHealthCommandService {

    private final AppointmentRepository appointments;
    private final AppointmentAnimalRepository appointmentAnimals;
    private final MedicalRecordRepository medicalRecords;
    private final MedicationRepository medications;
    private final TreatmentRepository treatments;
    private final VaccineRepository vaccines;
    private final Clock clock;
    private final IamContextFacade iam;
    private final VeterinaryAuthorizationService authorization;

    public VeterinaryAndHealthCommandServiceImpl(
            AppointmentRepository appointments,
            AppointmentAnimalRepository appointmentAnimals,
            MedicalRecordRepository medicalRecords,
            MedicationRepository medications,
            TreatmentRepository treatments,
            VaccineRepository vaccines,
            Clock clock,
            IamContextFacade iam,
            VeterinaryAuthorizationService authorization) {

        this.appointments = appointments;
        this.appointmentAnimals = appointmentAnimals;
        this.medicalRecords = medicalRecords;
        this.medications = medications;
        this.treatments = treatments;
        this.vaccines = vaccines;
        this.clock = clock;
        this.iam = iam;
        this.authorization = authorization;
    }

    @Override
    public Result<Appointment, VeterinaryAndHealthCommandFailure> handle(
            CreateAppointmentCommand command) {

        var appointment = Appointment.schedule(
                id(),
                command.rancherId(),
                command.veterinarianId(),
                command.requestedBy(),
                command.origin(),
                command.scheduledAt(),
                command.reason(),
                clock.instant()
        );

        return Result.success(appointments.save(appointment));
    }

    @Override
    public Result<Appointment, VeterinaryAndHealthCommandFailure> handle(
            UpdateAppointmentCommand command) {

        var found = appointments.findById(command.appointmentId());

        if (found.isEmpty()) {
            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.AppointmentNotFound(
                            command.appointmentId()));
        }

        var appointment = found.orElseThrow();

        Set<String> roles = iam.fetchRoleNamesByUserId(command.actorId())
                .orElseThrow(() -> new AccessDeniedException(
                        "Authenticated user does not exist"));

        boolean isRancher = roles.contains("ROLE_RANCHER");
        boolean isVeterinarian = roles.contains("ROLE_VETERINARIAN");

        if (isRancher == isVeterinarian) {
            throw new AccessDeniedException(
                    "User is not authorized to update veterinary appointments");
        }

        boolean isAppointmentRancher =
                isRancher
                        && appointment.getRancherId().value().equals(command.actorId());

        boolean isAssignedVeterinarian =
                isVeterinarian
                        && appointment.getVeterinarianId().value().equals(command.actorId());

        if (!isAppointmentRancher && !isAssignedVeterinarian) {
            throw new AccessDeniedException(
                    "You do not have permission to update this appointment");
        }

        if (command.reason() != null) {
            throw new AccessDeniedException(
                    "Changing the appointment reason is not allowed");
        }

        if (command.scheduledAt() != null && !isAppointmentRancher) {
            throw new AccessDeniedException(
                    "Only the rancher can reschedule an appointment");
        }

        if (command.status() != null) {

            boolean isCancellation =
                    command.status() == AppointmentStatus.cancelled;

            boolean isCompletion =
                    command.status() == AppointmentStatus.completed;

            if (!isCancellation && !(isCompletion && isAssignedVeterinarian)) {
                throw new AccessDeniedException(
                        "Only the assigned veterinarian can complete an appointment. "
                                + "Both parties may cancel it");
            }

            if (!appointment.canChangeTo(command.status())) {
                return Result.failure(
                        new VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus(
                                "Only scheduled appointments can change status"));
            }
        }

        if (command.scheduledAt() != null && !appointment.isScheduled()) {
            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.InvalidAppointmentStatus(
                            "Only scheduled appointments can be rescheduled"));
        }

        if (command.scheduledAt() != null) {
            appointment.reschedule(command.scheduledAt());
        }

        if (command.status() != null) {
            appointment.changeStatus(
                    command.status(),
                    command.cancellationReason()
            );
        }

        return Result.success(appointments.save(appointment));
    }

    @Override
    public Result<AppointmentAnimal, VeterinaryAndHealthCommandFailure> handle(
            LinkAnimalToAppointmentCommand command) {

        var appointment = appointments.findById(command.appointmentId());

        if (appointment.isEmpty()) {
            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.AppointmentNotFound(
                            command.appointmentId()));
        }

        authorization.requireAnimalLinkAccess(
                command.actorId(),
                command.appointmentId(),
                command.animalId().value()
        );

        var existing = appointmentAnimals.findByAppointmentIdAndAnimalId(
                command.appointmentId(),
                command.animalId()
        );

        if (existing.isPresent()) {
            return Result.success(existing.orElseThrow());
        }

        var link = AppointmentAnimal.link(
                id(),
                command.appointmentId(),
                command.animalId()
        );

        return Result.success(appointmentAnimals.save(link));
    }

    @Override
    public Result<MedicalRecord, VeterinaryAndHealthCommandFailure> handle(
            RegisterMedicalRecordCommand command) {

        if (command.appointmentId() != null
                && appointments.findById(command.appointmentId()).isEmpty()) {

            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.AppointmentNotFound(
                            command.appointmentId()));
        }

        authorization.requireMedicalRecordRegistration(
                command.actorId(),
                command.veterinarianId().value(),
                command.appointmentId(),
                command.animalId().value()
        );

        var record = MedicalRecord.register(
                id(),
                command.appointmentId(),
                command.animalId(),
                command.veterinarianId(),
                command.recordedAt(),
                command.symptoms(),
                command.diagnosis(),
                command.severity(),
                command.healthStatus(),
                command.notes(),
                clock.instant()
        );

        return Result.success(medicalRecords.save(record));
    }

    @Override
    public Result<Treatment, VeterinaryAndHealthCommandFailure> handle(
            PrescribeTreatmentCommand command) {

        var medicalRecordOptional =
                medicalRecords.findById(command.medicalRecordId());

        if (medicalRecordOptional.isEmpty()) {
            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.MedicalRecordNotFound(
                            command.medicalRecordId()));
        }

        var medicalRecord = medicalRecordOptional.orElseThrow();

        // Validate that the veterinarian can prescribe treatment
        // within this medical record and for this animal.
        authorization.requireTreatmentPrescription(
                command.actorId(),
                medicalRecord,
                command.animalId().value()
        );

        if (medications.findById(command.medicationId()).isEmpty()) {
            return Result.failure(
                    new VeterinaryAndHealthCommandFailure.MedicationNotFound(
                            command.medicationId()));
        }

        var treatment = Treatment.prescribe(
                id(),
                command.medicalRecordId(),
                command.animalId(),
                command.medicationId(),
                command.treatmentType(),
                command.dose(),
                command.route(),
                command.frequencyHours(),
                command.durationDays(),
                command.scheduledAt(),
                command.administeredAt(),
                command.nextDate(),
                command.instructions(),
                clock.instant()
        );

        return Result.success(treatments.save(treatment));
    }


    @Override
    public Result<Vaccine, VeterinaryAndHealthCommandFailure> handle(
            RegisterVaccineCommand command) {

        // Validate that the authenticated veterinarian is authorized
        // to register a vaccine for this animal within this appointment.
        authorization.requireVaccineRegistration(
                command.actorId(),
                command.veterinarianId().value(),
                command.appointmentId(),
                command.animalId().value()
        );

        var vaccine = Vaccine.register(
                id(),
                command.animalId(),
                command.veterinarianId(),
                command.vaccineType(),
                command.name(),
                command.dose(),
                command.laboratory(),
                command.lotNumber(),
                command.appliedAt(),
                command.nextDoseAt(),
                command.route(),
                command.applicationSite(),
                command.status(),
                command.observations(),
                clock.instant()
        );

        return Result.success(vaccines.save(vaccine));
    }


    private String id() {
        return UUID.randomUUID().toString();
    }
}

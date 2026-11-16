
package com.vantara.platform.veterinaryandhealth.application.internal.authorization;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.livestockmanagement.interfaces.acl.AnimalRecord;
import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentAnimalRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class VeterinaryAuthorizationService {

    private final AuthenticatedUserIdentity identity;
    private final IamContextFacade iam;
    private final LivestockContextFacade livestock;
    private final AppointmentRepository appointments;
    private final AppointmentAnimalRepository appointmentAnimals;

    public VeterinaryAuthorizationService(
            AuthenticatedUserIdentity identity,
            IamContextFacade iam,
            LivestockContextFacade livestock,
            AppointmentRepository appointments,
            AppointmentAnimalRepository appointmentAnimals) {

        this.identity = identity;
        this.iam = iam;
        this.livestock = livestock;
        this.appointments = appointments;
        this.appointmentAnimals = appointmentAnimals;
    }

    public void requireAppointmentAccess(
            Principal principal,
            Appointment appointment) {

        if (principal == null || appointment == null) {
            throw new AccessDeniedException("Appointment access denied");
        }

        Long userId = requireUserId(principal);

        boolean isRancher = identity.isRancher(principal)
                && appointment.getRancherId().value().equals(userId);

        boolean isAssignedVeterinarian = identity.isVeterinarian(principal)
                && appointment.getVeterinarianId().value().equals(userId);

        if (!isRancher && !isAssignedVeterinarian) {
            throw new AccessDeniedException(
                    "You are not authorized to access this appointment");
        }
    }

    public Appointment requireAppointmentAccess(
            Principal principal,
            String appointmentId) {

        if (appointmentId == null || appointmentId.isBlank()) {
            throw new AccessDeniedException("Appointment access denied");
        }

        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() ->
                        new AccessDeniedException("Appointment access denied"));

        requireAppointmentAccess(principal, appointment);
        return appointment;
    }

    public AnimalRecord requireAnimalAccessThroughAppointment(
            Principal principal,
            String appointmentId,
            String animalId) {

        Appointment appointment =
                requireAppointmentAccess(principal, appointmentId);

        if (animalId == null || animalId.isBlank()) {
            throw new AccessDeniedException("Animal access denied");
        }

        AnimalId id = new AnimalId(animalId);

        boolean linked = appointmentAnimals
                .findByAppointmentIdAndAnimalId(appointmentId, id)
                .isPresent();

        if (!linked) {
            throw new AccessDeniedException(
                    "Animal is not linked to this appointment");
        }

        AnimalRecord animal = livestock.findAnimalById(animalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Animal access denied"));

        boolean belongsToAppointmentRancher =
                animal.rancherId().equals(
                        String.valueOf(appointment.getRancherId().value()));

        if (!belongsToAppointmentRancher) {
            throw new AccessDeniedException(
                    "Animal does not belong to the appointment owner");
        }

        return animal;
    }

    public AnimalRecord requireAnimalAccess(
            Principal principal,
            String animalId) {

        if (principal == null || animalId == null || animalId.isBlank()) {
            throw new AccessDeniedException("Animal access denied");
        }

        Long userId = requireUserId(principal);

        AnimalRecord animal = livestock.findAnimalById(animalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Animal access denied"));

        boolean isOwner = identity.isRancher(principal)
                && animal.rancherId().equals(String.valueOf(userId));

        if (isOwner) {
            return animal;
        }

        if (identity.isVeterinarian(principal)) {
            AnimalId id = new AnimalId(animalId);

            boolean hasAssignedAppointment = appointmentAnimals
                    .findAllByAnimalId(id)
                    .stream()
                    .map(link ->
                            appointments.findById(link.getAppointmentId()))
                    .flatMap(java.util.Optional::stream)
                    .anyMatch(appointment ->
                            appointment.getVeterinarianId().value().equals(userId)
                                    && animal.rancherId().equals(
                                    String.valueOf(
                                            appointment.getRancherId().value()))
                    );

            if (hasAssignedAppointment) {
                return animal;
            }
        }

        throw new AccessDeniedException(
                "You are not authorized to access this animal");
    }

    /**
     * Authorizes linking an animal to an appointment.
     * The rancher must own the appointment.
     * The veterinarian must be assigned to the appointment.
     * In both cases, the animal must belong to the appointment's rancher.
     */
    public void requireAnimalLinkAccess(
            Long actorId,
            String appointmentId,
            String animalId) {

        if (actorId == null || actorId <= 0
                || appointmentId == null || appointmentId.isBlank()
                || animalId == null || animalId.isBlank()) {

            throw new AccessDeniedException("Animal link access denied");
        }

        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() ->
                        new AccessDeniedException("Appointment access denied"));

        Set<String> roles = iam.fetchRoleNamesByUserId(actorId)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        boolean isRancher = roles.contains("ROLE_RANCHER");
        boolean isVeterinarian = roles.contains("ROLE_VETERINARIAN");

        if (isRancher == isVeterinarian) {
            throw new AccessDeniedException(
                    "User is not authorized to link animals");
        }

        boolean ownsAppointment = isRancher
                && appointment.getRancherId().value().equals(actorId);

        boolean isAssignedVeterinarian = isVeterinarian
                && appointment.getVeterinarianId().value().equals(actorId);

        if (!ownsAppointment && !isAssignedVeterinarian) {
            throw new AccessDeniedException(
                    "You are not authorized to link animals to this appointment");
        }

        AnimalRecord animal = livestock.findAnimalById(animalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Animal access denied"));

        boolean belongsToAppointmentRancher =
                animal.rancherId().equals(
                        String.valueOf(appointment.getRancherId().value()));

        if (!belongsToAppointmentRancher) {
            throw new AccessDeniedException(
                    "Animal does not belong to the appointment owner");
        }
    }

    /**
     * Authorizes the registration of a medical record.
     * Only the authenticated veterinarian can register the record.
     * The veterinarian must have access to the animal.
     * If an appointment is provided, the animal must be linked to it.
     */
    public void requireMedicalRecordRegistration(
            Long actorId,
            Long veterinarianId,
            String appointmentId,
            String animalId) {

        if (actorId == null || actorId <= 0
                || veterinarianId == null
                || !actorId.equals(veterinarianId)
                || animalId == null || animalId.isBlank()) {

            throw new AccessDeniedException(
                    "Medical record registration denied");
        }

        Set<String> roles = iam.fetchRoleNamesByUserId(actorId)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        if (!roles.contains("ROLE_VETERINARIAN")
                || roles.contains("ROLE_RANCHER")) {

            throw new AccessDeniedException(
                    "Only veterinarians can register medical records");
        }

        AnimalRecord animal = livestock.findAnimalById(animalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Animal access denied"));

        if (appointmentId != null && !appointmentId.isBlank()) {

            Appointment appointment = appointments.findById(appointmentId)
                    .orElseThrow(() ->
                            new AccessDeniedException("Appointment access denied"));

            boolean isAssignedVeterinarian =
                    appointment.getVeterinarianId().value().equals(actorId);

            boolean belongsToAppointmentRancher =
                    animal.rancherId().equals(
                            String.valueOf(appointment.getRancherId().value()));

            boolean isLinked = appointmentAnimals
                    .findByAppointmentIdAndAnimalId(
                            appointmentId,
                            new AnimalId(animalId))
                    .isPresent();

            if (!isAssignedVeterinarian
                    || !belongsToAppointmentRancher
                    || !isLinked) {

                throw new AccessDeniedException(
                        "You are not authorized to register a medical record for this animal");
            }

            return;
        }

        boolean hasAssignedAppointment = appointmentAnimals
                .findAllByAnimalId(new AnimalId(animalId))
                .stream()
                .map(link ->
                        appointments.findById(link.getAppointmentId()))
                .flatMap(java.util.Optional::stream)
                .anyMatch(appointment ->
                        appointment.getVeterinarianId().value().equals(actorId)
                                && animal.rancherId().equals(
                                String.valueOf(
                                        appointment.getRancherId().value()))
                );

        if (!hasAssignedAppointment) {
            throw new AccessDeniedException(
                    "You do not have access to this animal");
        }
    }

    /**
     * Authorizes prescribing a treatment within a medical record.
     * The veterinarian must be the one who registered the medical record
     * and must have access to the animal.
     */
    public void requireTreatmentPrescription(
            Long actorId,
            MedicalRecord medicalRecord,
            String animalId) {

        if (medicalRecord == null
                || animalId == null
                || animalId.isBlank()) {

            throw new AccessDeniedException(
                    "Treatment prescription access denied");
        }

        boolean belongsToMedicalRecord =
                medicalRecord.getAnimalId().value().equals(animalId);

        if (!belongsToMedicalRecord) {
            throw new AccessDeniedException(
                    "The treatment animal does not match the medical record");
        }

        requireMedicalRecordRegistration(
                actorId,
                medicalRecord.getVeterinarianId().value(),
                medicalRecord.getAppointmentId(),
                animalId
        );
    }

    /**
     * Authorizes registering a vaccine.
     * The authenticated veterinarian must be assigned to the specified
     * appointment, and the animal must be linked to that appointment.
     */
    public void requireVaccineRegistration(
            Long actorId,
            Long veterinarianId,
            String appointmentId,
            String animalId) {

        if (appointmentId == null || appointmentId.isBlank()) {
            throw new AccessDeniedException(
                    "An appointment is required to register a vaccine");
        }

        requireMedicalRecordRegistration(
                actorId,
                veterinarianId,
                appointmentId,
                animalId
        );
    }

    private Long requireUserId(Principal principal) {

        if (principal == null) {
            throw new AccessDeniedException("Authentication is required");
        }

        return identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));
    }

    public boolean canAccessAnimal(
            Principal principal,
            String animalId) {

        try {
            requireAnimalAccess(principal, animalId);
            return true;
        } catch (AccessDeniedException exception) {
            return false;
        }
    }

    public boolean canAccessAppointment(
            Principal principal,
            Appointment appointment) {

        if (principal == null || appointment == null) {
            return false;
        }

        Long userId = identity.userId(principal)
                .map(Long::valueOf)
                .orElse(null);

        if (userId == null) {
            return false;
        }

        boolean isRancher = identity.isRancher(principal)
                && userId.equals(appointment.getRancherId().value());

        boolean isAssignedVeterinarian = identity.isVeterinarian(principal)
                && userId.equals(appointment.getVeterinarianId().value());

        return isRancher || isAssignedVeterinarian;
    }

    /**
     * Returns only animals linked to appointments assigned
     * to the authenticated veterinarian.
     */

    /**
     * Returns animals linked to appointments assigned
     * to the authenticated veterinarian.
     *
     * Each animal is returned once, together with one
     * valid appointment assigned to that veterinarian.
     */
    public List<AccessibleAnimalRecord> findAnimalsAccessibleToVeterinarian(
            Principal principal) {

        if (principal == null || !identity.isVeterinarian(principal)) {
            throw new AccessDeniedException(
                    "Only veterinarians can access this animal list");
        }

        Long userId = requireUserId(principal);

        Map<String, AccessibleAnimalRecord> accessibleAnimals =
                new LinkedHashMap<>();

        appointments.findAll().stream()
                .filter(appointment ->
                        appointment.getVeterinarianId().value().equals(userId))
                .forEach(appointment -> {

                    appointmentAnimals
                            .findAllByAppointmentId(appointment.getId())
                            .forEach(link -> {

                                livestock.findAnimalById(
                                        link.getAnimalId().value()
                                ).filter(animal ->
                                        animal.rancherId().equals(
                                                String.valueOf(
                                                        appointment.getRancherId().value()
                                                )
                                        )
                                ).ifPresent(animal ->
                                        accessibleAnimals.putIfAbsent(
                                                animal.id(),
                                                new AccessibleAnimalRecord(
                                                        appointment.getId(),
                                                        animal
                                                )
                                        )
                                );
                            });
                });

        return new ArrayList<>(accessibleAnimals.values());
    }

}

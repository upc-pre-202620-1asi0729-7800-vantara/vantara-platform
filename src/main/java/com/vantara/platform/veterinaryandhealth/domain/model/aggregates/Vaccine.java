package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.vantara.platform.veterinaryandhealth.domain.model.events.VaccineAppliedEvent;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.VaccineStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Aggregate root of a vaccine dose applied to an animal by a veterinarian.
 */
public class Vaccine extends AbstractDomainAggregateRoot<Vaccine> {

    private final String id;
    private final AnimalId animalId;
    private final UserId veterinarianId;
    private final String vaccineType;
    private final String name;
    private final Dose dose;
    private final String laboratory;
    private final String lotNumber;
    private final LocalDate appliedAt;
    private final LocalDate nextDoseAt;
    private final String route;
    private final String applicationSite;
    private final VaccineStatus status;
    private final String observations;

    private Vaccine(String id, AnimalId animalId, UserId veterinarianId, String vaccineType, String name,
                    Dose dose, String laboratory, String lotNumber, LocalDate appliedAt, LocalDate nextDoseAt,
                    String route, String applicationSite, VaccineStatus status, String observations) {
        this.id = Objects.requireNonNull(id, "Vaccine id cannot be null");
        this.animalId = Objects.requireNonNull(animalId, "Animal id cannot be null");
        this.veterinarianId = Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        this.vaccineType = optionalText(vaccineType);
        this.name = requireText(name, "Vaccine name");
        this.dose = Objects.requireNonNull(dose, "Dose cannot be null");
        this.laboratory = optionalText(laboratory);
        this.lotNumber = optionalText(lotNumber);
        this.appliedAt = Objects.requireNonNull(appliedAt, "Application date cannot be null");
        if (nextDoseAt != null && !nextDoseAt.isAfter(appliedAt)) {
            throw new IllegalArgumentException("Next dose date must be after the application date");
        }
        this.nextDoseAt = nextDoseAt;
        this.route = optionalText(route);
        this.applicationSite = optionalText(applicationSite);
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.observations = optionalText(observations);
    }

    /**
     * Registers a vaccine dose. A completed dose records a {@link VaccineAppliedEvent}.
     */
    public static Vaccine register(String id, AnimalId animalId, UserId veterinarianId, String vaccineType,
                                   String name, Dose dose, String laboratory, String lotNumber,
                                   LocalDate appliedAt, LocalDate nextDoseAt, String route, String applicationSite,
                                   VaccineStatus status, String observations, Instant now) {
        Objects.requireNonNull(now, "Registration instant cannot be null");
        var vaccine = new Vaccine(id, animalId, veterinarianId, vaccineType, name, dose, laboratory, lotNumber,
                appliedAt, nextDoseAt, route, applicationSite, status, observations);
        if (status == VaccineStatus.completed) {
            vaccine.registerDomainEvent(new VaccineAppliedEvent(id, animalId.value(), nextDoseAt, now));
        }
        return vaccine;
    }

    /**
     * Rebuilds a vaccine dose from its persisted state. It does not record any domain event.
     */
    public static Vaccine reconstitute(String id, AnimalId animalId, UserId veterinarianId, String vaccineType,
                                       String name, Dose dose, String laboratory, String lotNumber,
                                       LocalDate appliedAt, LocalDate nextDoseAt, String route,
                                       String applicationSite, VaccineStatus status, String observations) {
        return new Vaccine(id, animalId, veterinarianId, vaccineType, name, dose, laboratory, lotNumber,
                appliedAt, nextDoseAt, route, applicationSite, status, observations);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getId() {
        return id;
    }

    public AnimalId getAnimalId() {
        return animalId;
    }

    public UserId getVeterinarianId() {
        return veterinarianId;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public String getName() {
        return name;
    }

    public Dose getDose() {
        return dose;
    }

    public String getLaboratory() {
        return laboratory;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public LocalDate getAppliedAt() {
        return appliedAt;
    }

    public LocalDate getNextDoseAt() {
        return nextDoseAt;
    }

    public String getRoute() {
        return route;
    }

    public String getApplicationSite() {
        return applicationSite;
    }

    public VaccineStatus getStatus() {
        return status;
    }

    public String getObservations() {
        return observations;
    }
}

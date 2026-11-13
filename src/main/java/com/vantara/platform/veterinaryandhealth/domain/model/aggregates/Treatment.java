package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.vantara.platform.veterinaryandhealth.domain.model.events.TreatmentPrescribedEvent;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.Dose;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.TreatmentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate root of a medical treatment prescribed within a clinical record.
 * It references its clinical record and its medication by id.
 */
public class Treatment extends AbstractDomainAggregateRoot<Treatment> {

    private static final int TEXT_MAX_LENGTH = 1000;

    private final String id;
    private final String medicalRecordId;
    private final AnimalId animalId;
    private final String medicationId;
    private final String treatmentType;
    private final Dose dose;
    private final String route;
    private final int frequencyHours;
    private final int durationDays;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime administeredAt;
    private final LocalDate nextDate;
    private final TreatmentStatus status;
    private final String instructions;

    private Treatment(String id, String medicalRecordId, AnimalId animalId, String medicationId,
                      String treatmentType, Dose dose, String route, int frequencyHours, int durationDays,
                      LocalDateTime scheduledAt, LocalDateTime administeredAt, LocalDate nextDate,
                      TreatmentStatus status, String instructions) {
        this.id = Objects.requireNonNull(id, "Treatment id cannot be null");
        this.medicalRecordId = requireText(medicalRecordId, "Medical record id");
        this.animalId = Objects.requireNonNull(animalId, "Animal id cannot be null");
        this.medicationId = requireText(medicationId, "Medication id");
        this.treatmentType = requireText(treatmentType, "Treatment type");
        this.dose = Objects.requireNonNull(dose, "Dose cannot be null");
        this.route = requireText(route, "Route");
        if (frequencyHours <= 0) {
            throw new IllegalArgumentException("Frequency in hours must be greater than zero");
        }
        if (durationDays <= 0) {
            throw new IllegalArgumentException("Duration in days must be greater than zero");
        }
        this.frequencyHours = frequencyHours;
        this.durationDays = durationDays;
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "Scheduled date cannot be null");
        this.administeredAt = administeredAt;
        this.nextDate = nextDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.instructions = instructions == null || instructions.isBlank() ? null : requireText(instructions, "Instructions");
    }

    /**
     * Prescribes an active treatment and records a {@link TreatmentPrescribedEvent}.
     */
    public static Treatment prescribe(String id, String medicalRecordId, AnimalId animalId, String medicationId,
                                      String treatmentType, Dose dose, String route, int frequencyHours,
                                      int durationDays, LocalDateTime scheduledAt, LocalDateTime administeredAt,
                                      LocalDate nextDate, String instructions, Instant now) {
        Objects.requireNonNull(now, "Prescription instant cannot be null");
        var treatment = new Treatment(id, medicalRecordId, animalId, medicationId, treatmentType, dose, route,
                frequencyHours, durationDays, scheduledAt, administeredAt, nextDate, TreatmentStatus.active,
                instructions);
        treatment.registerDomainEvent(new TreatmentPrescribedEvent(
                id, animalId.value(), medicationId, now));
        return treatment;
    }

    /**
     * Rebuilds a treatment from its persisted state. It does not record any domain event.
     */
    public static Treatment reconstitute(String id, String medicalRecordId, AnimalId animalId, String medicationId,
                                         String treatmentType, Dose dose, String route, int frequencyHours,
                                         int durationDays, LocalDateTime scheduledAt, LocalDateTime administeredAt,
                                         LocalDate nextDate, TreatmentStatus status, String instructions) {
        return new Treatment(id, medicalRecordId, animalId, medicationId, treatmentType, dose, route,
                frequencyHours, durationDays, scheduledAt, administeredAt, nextDate, status, instructions);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        var trimmed = value.trim();
        if (trimmed.length() > TEXT_MAX_LENGTH) {
            throw new IllegalArgumentException(label + " cannot exceed 1000 characters");
        }
        return trimmed;
    }

    public String getId() {
        return id;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public AnimalId getAnimalId() {
        return animalId;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public Dose getDose() {
        return dose;
    }

    public String getRoute() {
        return route;
    }

    public int getFrequencyHours() {
        return frequencyHours;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getAdministeredAt() {
        return administeredAt;
    }

    public LocalDate getNextDate() {
        return nextDate;
    }

    public TreatmentStatus getStatus() {
        return status;
    }

    public String getInstructions() {
        return instructions;
    }
}

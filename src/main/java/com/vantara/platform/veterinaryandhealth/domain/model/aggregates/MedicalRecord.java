package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.vantara.platform.veterinaryandhealth.domain.model.events.MedicalRecordRegisteredEvent;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalHealthStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.MedicalRecordSeverity;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate root of a clinical record: the diagnosis a veterinarian issues for an animal,
 * optionally linked to the appointment that motivated it.
 */
public class MedicalRecord extends AbstractDomainAggregateRoot<MedicalRecord> {

    private static final int TEXT_MAX_LENGTH = 1000;

    private final String id;
    private final String appointmentId;
    private final AnimalId animalId;
    private final UserId veterinarianId;
    private final LocalDateTime recordedAt;
    private final String symptoms;
    private final String diagnosis;
    private final MedicalRecordSeverity severity;
    private final AnimalHealthStatus healthStatus;
    private final String notes;

    private MedicalRecord(String id, String appointmentId, AnimalId animalId, UserId veterinarianId,
                          LocalDateTime recordedAt, String symptoms, String diagnosis,
                          MedicalRecordSeverity severity, AnimalHealthStatus healthStatus, String notes) {
        this.id = Objects.requireNonNull(id, "Medical record id cannot be null");
        this.appointmentId = appointmentId == null || appointmentId.isBlank() ? null : appointmentId.trim();
        this.animalId = Objects.requireNonNull(animalId, "Animal id cannot be null");
        this.veterinarianId = Objects.requireNonNull(veterinarianId, "Veterinarian id cannot be null");
        this.recordedAt = Objects.requireNonNull(recordedAt, "Recorded date cannot be null");
        this.symptoms = requireText(symptoms, "Symptoms");
        this.diagnosis = requireText(diagnosis, "Diagnosis");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.healthStatus = Objects.requireNonNull(healthStatus, "Health status cannot be null");
        this.notes = notes == null || notes.isBlank() ? null : requireText(notes, "Notes");
    }

    /**
     * Registers a clinical record and records a {@link MedicalRecordRegisteredEvent}.
     */
    public static MedicalRecord register(String id, String appointmentId, AnimalId animalId, UserId veterinarianId,
                                         LocalDateTime recordedAt, String symptoms, String diagnosis,
                                         MedicalRecordSeverity severity, AnimalHealthStatus healthStatus,
                                         String notes, Instant now) {
        Objects.requireNonNull(now, "Registration instant cannot be null");
        var record = new MedicalRecord(id, appointmentId, animalId, veterinarianId, recordedAt, symptoms,
                diagnosis, severity, healthStatus, notes);
        record.registerDomainEvent(new MedicalRecordRegisteredEvent(
                id, animalId.value(), severity.name(), healthStatus.name(), now));
        return record;
    }

    /**
     * Rebuilds a clinical record from its persisted state. It does not record any domain event.
     */
    public static MedicalRecord reconstitute(String id, String appointmentId, AnimalId animalId, UserId veterinarianId,
                                             LocalDateTime recordedAt, String symptoms, String diagnosis,
                                             MedicalRecordSeverity severity, AnimalHealthStatus healthStatus,
                                             String notes) {
        return new MedicalRecord(id, appointmentId, animalId, veterinarianId, recordedAt, symptoms,
                diagnosis, severity, healthStatus, notes);
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

    public String getAppointmentId() {
        return appointmentId;
    }

    public AnimalId getAnimalId() {
        return animalId;
    }

    public UserId getVeterinarianId() {
        return veterinarianId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public MedicalRecordSeverity getSeverity() {
        return severity;
    }

    public AnimalHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public String getNotes() {
        return notes;
    }
}

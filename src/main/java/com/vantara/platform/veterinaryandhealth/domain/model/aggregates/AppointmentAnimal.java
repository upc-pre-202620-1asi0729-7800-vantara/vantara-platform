package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.Objects;

/**
 * Association between a veterinary appointment and an animal. It references both sides by id,
 * so the appointment and the animal keep their own lifecycles.
 */
public class AppointmentAnimal extends AbstractDomainAggregateRoot<AppointmentAnimal> {

    private final String id;
    private final String appointmentId;
    private final AnimalId animalId;

    private AppointmentAnimal(String id, String appointmentId, AnimalId animalId) {
        this.id = Objects.requireNonNull(id, "Appointment animal id cannot be null");
        this.appointmentId = requireText(appointmentId, "Appointment id");
        this.animalId = Objects.requireNonNull(animalId, "Animal id cannot be null");
    }

    public static AppointmentAnimal link(String id, String appointmentId, AnimalId animalId) {
        return new AppointmentAnimal(id, appointmentId, animalId);
    }

    public static AppointmentAnimal reconstitute(String id, String appointmentId, AnimalId animalId) {
        return new AppointmentAnimal(id, appointmentId, animalId);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
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
}

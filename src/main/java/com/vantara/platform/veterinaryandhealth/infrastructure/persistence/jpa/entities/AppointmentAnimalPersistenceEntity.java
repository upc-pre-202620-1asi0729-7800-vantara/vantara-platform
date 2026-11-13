package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity of the link between an appointment and an animal. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointment_animals",
        uniqueConstraints = @UniqueConstraint(name = "uk_appointment_animals_appointment_animal",
                columnNames = {"appointment_id", "animal_id"}))
public class AppointmentAnimalPersistenceEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String appointmentId;

    @Column(nullable = false, length = 36)
    private String animalId;
}

package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.AppointmentAnimalPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentAnimalPersistenceRepository extends JpaRepository<AppointmentAnimalPersistenceEntity, String> {

    Optional<AppointmentAnimalPersistenceEntity> findByAppointmentIdAndAnimalId(String appointmentId, String animalId);

    List<AppointmentAnimalPersistenceEntity> findAllByAppointmentId(String appointmentId);

    List<AppointmentAnimalPersistenceEntity> findAllByAnimalId(String animalId);
}

package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for {@link AppointmentAnimal} aggregates.
 */
public interface AppointmentAnimalRepository {

    AppointmentAnimal save(AppointmentAnimal appointmentAnimal);

    Optional<AppointmentAnimal> findByAppointmentIdAndAnimalId(String appointmentId, AnimalId animalId);

    List<AppointmentAnimal> findAll();

    List<AppointmentAnimal> findAllByAppointmentId(String appointmentId);

    List<AppointmentAnimal> findAllByAnimalId(AnimalId animalId);
}

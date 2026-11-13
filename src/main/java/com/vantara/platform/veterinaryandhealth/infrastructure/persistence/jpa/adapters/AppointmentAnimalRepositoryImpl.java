package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentAnimalRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.AppointmentAnimalPersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.AppointmentAnimalPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link AppointmentAnimalRepository} domain port with Spring Data JPA.
 */
@Repository
public class AppointmentAnimalRepositoryImpl implements AppointmentAnimalRepository {

    private final AppointmentAnimalPersistenceRepository persistenceRepository;

    public AppointmentAnimalRepositoryImpl(AppointmentAnimalPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public AppointmentAnimal save(AppointmentAnimal appointmentAnimal) {
        var entity = persistenceRepository.save(
                AppointmentAnimalPersistenceAssembler.toPersistenceFromDomain(appointmentAnimal));
        return AppointmentAnimalPersistenceAssembler.toDomainFromPersistence(entity);
    }

    @Override
    public Optional<AppointmentAnimal> findByAppointmentIdAndAnimalId(String appointmentId, AnimalId animalId) {
        return persistenceRepository.findByAppointmentIdAndAnimalId(appointmentId, animalId.value())
                .map(AppointmentAnimalPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<AppointmentAnimal> findAll() {
        return persistenceRepository.findAll().stream()
                .map(AppointmentAnimalPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<AppointmentAnimal> findAllByAppointmentId(String appointmentId) {
        return persistenceRepository.findAllByAppointmentId(appointmentId).stream()
                .map(AppointmentAnimalPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<AppointmentAnimal> findAllByAnimalId(AnimalId animalId) {
        return persistenceRepository.findAllByAnimalId(animalId.value()).stream()
                .map(AppointmentAnimalPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

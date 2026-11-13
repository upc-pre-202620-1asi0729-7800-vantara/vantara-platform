package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.repositories.VaccineRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.VaccinePersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.VaccinePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter that implements the {@link VaccineRepository} domain port with Spring Data JPA.
 */
@Repository
public class VaccineRepositoryImpl implements VaccineRepository {

    private final VaccinePersistenceRepository persistenceRepository;

    public VaccineRepositoryImpl(VaccinePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Vaccine save(Vaccine vaccine) {
        var entity = persistenceRepository.save(VaccinePersistenceAssembler.toPersistenceFromDomain(vaccine));
        return VaccinePersistenceAssembler.toDomainFromPersistence(entity);
    }

    @Override
    public List<Vaccine> findAll() {
        return persistenceRepository.findAll().stream()
                .map(VaccinePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Vaccine> findAllByAnimalId(AnimalId animalId) {
        return persistenceRepository.findAllByAnimalId(animalId.value()).stream()
                .map(VaccinePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

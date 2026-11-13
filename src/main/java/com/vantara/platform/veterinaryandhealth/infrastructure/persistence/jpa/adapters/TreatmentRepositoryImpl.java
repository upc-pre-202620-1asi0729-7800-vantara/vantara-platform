package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.repositories.TreatmentRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.TreatmentPersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.TreatmentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter that implements the {@link TreatmentRepository} domain port with Spring Data JPA.
 */
@Repository
public class TreatmentRepositoryImpl implements TreatmentRepository {

    private final TreatmentPersistenceRepository persistenceRepository;

    public TreatmentRepositoryImpl(TreatmentPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Treatment save(Treatment treatment) {
        var entity = persistenceRepository.save(TreatmentPersistenceAssembler.toPersistenceFromDomain(treatment));
        return TreatmentPersistenceAssembler.toDomainFromPersistence(entity);
    }

    @Override
    public List<Treatment> findAll() {
        return persistenceRepository.findAll().stream()
                .map(TreatmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Treatment> findAllByAnimalId(AnimalId animalId) {
        return persistenceRepository.findAllByAnimalId(animalId.value()).stream()
                .map(TreatmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

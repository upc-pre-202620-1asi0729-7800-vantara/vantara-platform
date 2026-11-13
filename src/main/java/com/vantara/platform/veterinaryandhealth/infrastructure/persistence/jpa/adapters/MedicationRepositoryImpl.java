package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Medication;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicationRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.MedicationPersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.MedicationPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the read-only {@link MedicationRepository} domain port with Spring Data JPA.
 */
@Repository
public class MedicationRepositoryImpl implements MedicationRepository {

    private final MedicationPersistenceRepository persistenceRepository;

    public MedicationRepositoryImpl(MedicationPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Medication> findById(String id) {
        return persistenceRepository.findById(id).map(MedicationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Medication> findAll() {
        return persistenceRepository.findAll().stream()
                .map(MedicationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

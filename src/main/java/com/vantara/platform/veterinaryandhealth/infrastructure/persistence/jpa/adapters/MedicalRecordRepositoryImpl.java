package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicalRecordRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.MedicalRecordPersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.MedicalRecordPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link MedicalRecordRepository} domain port with Spring Data JPA.
 */
@Repository
public class MedicalRecordRepositoryImpl implements MedicalRecordRepository {

    private final MedicalRecordPersistenceRepository persistenceRepository;

    public MedicalRecordRepositoryImpl(MedicalRecordPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        var entity = persistenceRepository.save(MedicalRecordPersistenceAssembler.toPersistenceFromDomain(medicalRecord));
        return MedicalRecordPersistenceAssembler.toDomainFromPersistence(entity);
    }

    @Override
    public Optional<MedicalRecord> findById(String id) {
        return persistenceRepository.findById(id).map(MedicalRecordPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<MedicalRecord> findAll() {
        return persistenceRepository.findAll().stream()
                .map(MedicalRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<MedicalRecord> findAllByAnimalId(AnimalId animalId) {
        return persistenceRepository.findAllByAnimalId(animalId.value()).stream()
                .map(MedicalRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

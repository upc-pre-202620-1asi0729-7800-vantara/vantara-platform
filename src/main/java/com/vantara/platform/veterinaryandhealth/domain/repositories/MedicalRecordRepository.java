package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for {@link MedicalRecord} aggregates.
 */
public interface MedicalRecordRepository {

    MedicalRecord save(MedicalRecord medicalRecord);

    Optional<MedicalRecord> findById(String id);

    List<MedicalRecord> findAll();

    List<MedicalRecord> findAllByAnimalId(AnimalId animalId);
}

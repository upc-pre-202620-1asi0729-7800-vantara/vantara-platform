package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.MedicalRecordPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordPersistenceRepository extends JpaRepository<MedicalRecordPersistenceEntity, String> {

    List<MedicalRecordPersistenceEntity> findAllByAnimalId(String animalId);
}

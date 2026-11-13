package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.TreatmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentPersistenceRepository extends JpaRepository<TreatmentPersistenceEntity, String> {

    List<TreatmentPersistenceEntity> findAllByAnimalId(String animalId);
}

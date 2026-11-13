package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.VaccinePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinePersistenceRepository extends JpaRepository<VaccinePersistenceEntity, String> {

    List<VaccinePersistenceEntity> findAllByAnimalId(String animalId);
}

package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.MedicationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationPersistenceRepository extends JpaRepository<MedicationPersistenceEntity, String> {
}

package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.AppointmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentPersistenceRepository extends JpaRepository<AppointmentPersistenceEntity, String> {
}

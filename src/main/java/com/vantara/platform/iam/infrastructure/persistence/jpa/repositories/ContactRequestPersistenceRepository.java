package com.vantara.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.ContactRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRequestPersistenceRepository extends JpaRepository<ContactRequestPersistenceEntity, Long> {
}

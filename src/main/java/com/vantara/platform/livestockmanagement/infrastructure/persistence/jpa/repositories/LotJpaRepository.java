package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.LotJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotJpaRepository extends JpaRepository<LotJpaEntity, String> { }

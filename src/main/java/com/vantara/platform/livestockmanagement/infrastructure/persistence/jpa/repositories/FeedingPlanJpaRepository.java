package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedingPlanJpaRepository extends JpaRepository<FeedingPlanJpaEntity, String> { }

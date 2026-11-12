package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedingPlanItemJpaRepository extends JpaRepository<FeedingPlanItemJpaEntity, String> {
    List<FeedingPlanItemJpaEntity> findAllByFeedingPlanId(String feedingPlanId);
}

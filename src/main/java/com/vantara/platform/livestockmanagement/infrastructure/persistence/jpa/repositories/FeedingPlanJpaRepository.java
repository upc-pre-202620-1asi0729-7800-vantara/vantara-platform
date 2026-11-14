package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FeedingPlanJpaRepository extends JpaRepository<FeedingPlanJpaEntity, String> {

    List<FeedingPlanJpaEntity> findAllByLotIdIn(Collection<String> lotIds);
}

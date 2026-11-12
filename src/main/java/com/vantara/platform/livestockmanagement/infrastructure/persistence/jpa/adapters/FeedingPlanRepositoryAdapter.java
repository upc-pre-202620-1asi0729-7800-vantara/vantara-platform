package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingPlanJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedingPlanRepositoryAdapter implements FeedingPlanRepository {
    private final FeedingPlanJpaRepository repository;
    public FeedingPlanRepositoryAdapter(FeedingPlanJpaRepository repository) { this.repository = repository; }
    public FeedingPlan save(FeedingPlan plan) { return repository.save(FeedingPlanJpaEntity.fromDomain(plan)).toDomain(); }
    public Optional<FeedingPlan> findById(String id) { return repository.findById(id).map(FeedingPlanJpaEntity::toDomain); }
    public List<FeedingPlan> findAll() { return repository.findAll().stream().map(FeedingPlanJpaEntity::toDomain).toList(); }
}

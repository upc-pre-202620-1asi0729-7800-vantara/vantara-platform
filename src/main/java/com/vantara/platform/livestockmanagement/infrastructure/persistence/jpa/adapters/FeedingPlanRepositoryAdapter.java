
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.FeedingPlanMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingPlanJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FeedingPlanRepositoryAdapter implements FeedingPlanRepository {

    private final FeedingPlanJpaRepository repository;

    public FeedingPlanRepositoryAdapter(
            FeedingPlanJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public FeedingPlan save(FeedingPlan plan) {
        FeedingPlanJpaEntity entity = FeedingPlanMapper.toEntity(plan);

        FeedingPlanJpaEntity savedEntity = repository.save(entity);

        return FeedingPlanMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<FeedingPlan> findById(String id) {
        return repository.findById(id)
                .map(FeedingPlanMapper::toDomain);
    }

    @Override
    public List<FeedingPlan> findAll() {
        return repository.findAll()
                .stream()
                .map(FeedingPlanMapper::toDomain)
                .toList();
    }
}

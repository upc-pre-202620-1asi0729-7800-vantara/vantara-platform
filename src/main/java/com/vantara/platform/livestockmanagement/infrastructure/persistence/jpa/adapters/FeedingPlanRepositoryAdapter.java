
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.FeedingPlanMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingPlanJpaRepository;

import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FeedingPlanRepositoryAdapter implements FeedingPlanRepository {

    private final FeedingPlanJpaRepository repository;
    private final DomainEventPublisher eventPublisher;

    public FeedingPlanRepositoryAdapter(
            FeedingPlanJpaRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public FeedingPlan save(FeedingPlan plan) {
        FeedingPlanJpaEntity entity = FeedingPlanMapper.toEntity(plan);

        FeedingPlanJpaEntity savedEntity = repository.save(entity);
        var saved = FeedingPlanMapper.toDomain(savedEntity);

        eventPublisher.publishAndClear(plan);

        return saved;
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

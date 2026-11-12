package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanItemRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanItemJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingPlanItemJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FeedingPlanItemRepositoryAdapter implements FeedingPlanItemRepository {
    private final FeedingPlanItemJpaRepository repository;
    public FeedingPlanItemRepositoryAdapter(FeedingPlanItemJpaRepository repository) { this.repository = repository; }
    public FeedingPlanItem save(FeedingPlanItem item) { return repository.save(FeedingPlanItemJpaEntity.fromDomain(item)).toDomain(); }
    public List<FeedingPlanItem> findAll() { return repository.findAll().stream().map(FeedingPlanItemJpaEntity::toDomain).toList(); }
    public List<FeedingPlanItem> findAllByFeedingPlanId(String feedingPlanId) { return repository.findAllByFeedingPlanId(feedingPlanId).stream().map(FeedingPlanItemJpaEntity::toDomain).toList(); }
}

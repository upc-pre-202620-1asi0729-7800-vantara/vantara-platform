package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import java.util.List;

/** Persistence port for the ingredients of a feeding plan. */
public interface FeedingPlanItemRepository {
    FeedingPlanItem save(FeedingPlanItem item);
    List<FeedingPlanItem> findAll();
    List<FeedingPlanItem> findAllByFeedingPlanId(String feedingPlanId);
}

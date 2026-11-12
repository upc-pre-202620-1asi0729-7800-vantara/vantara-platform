package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for feeding plans.
 */
public interface FeedingPlanRepository {

    FeedingPlan save(FeedingPlan plan);

    Optional<FeedingPlan> findById(String id);

    List<FeedingPlan> findAll();
}
package com.vantara.platform.livestockmanagement.application.queryservices;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import java.util.List;
import java.util.Optional;

/** Query contract for the livestock-management read model. */
public interface LivestockManagementQueryService {
    List<Animal> getAnimals(); Optional<Animal> getAnimal(String id);
    List<Lot> getLots(); Optional<Lot> getLot(String id);
    List<Movement> getMovements(String animalId);
    List<FeedingPlan> getFeedingPlans(); Optional<FeedingPlan> getFeedingPlan(String id);
    List<FeedingPlanItem> getFeedingPlanItems(String feedingPlanId);
    List<FeedingLog> getFeedingLogs(String lotId, String animalId);
}

package com.vantara.platform.livestockmanagement.application.commandservices;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingLog;
import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;

/** Command contract for livestock registration and lifecycle operations. */
public interface LivestockManagementCommandService {
    Animal createAnimal(Animal animal);
    Animal updateAnimal(String id, Animal replacement);
    void deleteAnimal(String id);
    Lot createLot(Lot lot);
    Movement recordMovement(Movement movement);
    FeedingPlan createFeedingPlan(FeedingPlan plan);
    FeedingPlanItem createFeedingPlanItem(FeedingPlanItem item);
    FeedingLog recordFeeding(FeedingLog log);
}

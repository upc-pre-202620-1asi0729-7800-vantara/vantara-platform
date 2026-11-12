package com.vantara.platform.livestockmanagement.application.queryservices;

import com.vantara.platform.livestockmanagement.application.queries.*;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import java.util.List;
import java.util.Optional;

/** Query contract for the livestock-management read model. */
public interface LivestockManagementQueryService {
    List<Animal> handle(GetAllAnimalsQuery query);
    Optional<Animal> handle(GetAnimalByIdQuery query);
    List<Lot> handle(GetAllLotsQuery query);
    Optional<Lot> handle(GetLotByIdQuery query);
    List<Movement> handle(GetMovementsQuery query);
    List<FeedingPlan> handle(GetAllFeedingPlansQuery query);
    Optional<FeedingPlan> handle(GetFeedingPlanByIdQuery query);
    List<FeedingPlanItem> handle(GetFeedingPlanItemsQuery query);
    List<FeedingLog> handle(GetFeedingLogsQuery query);
}

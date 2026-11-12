
package com.vantara.platform.livestockmanagement.application.queryservices;

import com.vantara.platform.livestockmanagement.application.queries.GetAllAnimalsQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetAnimalByIdQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetAllLotsQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetLotByIdQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetMovementsQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetAllFeedingPlansQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetFeedingPlanByIdQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetFeedingPlanItemsQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetFeedingLogsQuery;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;
import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;

import java.util.List;
import java.util.Optional;

/**
 * Query contract for the livestock-management read model.
 */
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

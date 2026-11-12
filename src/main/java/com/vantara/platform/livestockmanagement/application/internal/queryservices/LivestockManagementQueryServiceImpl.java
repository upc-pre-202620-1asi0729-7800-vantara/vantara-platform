
package com.vantara.platform.livestockmanagement.application.internal.queryservices;

import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;

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

import com.vantara.platform.livestockmanagement.domain.repositories.AnimalRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.LotRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.MovementRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Handles read operations for livestock management.
 */
@Service
@Transactional(readOnly = true)
public class LivestockManagementQueryServiceImpl
        implements LivestockManagementQueryService {

    private final AnimalRepository animals;
    private final LotRepository lots;
    private final MovementRepository movements;
    private final FeedingPlanRepository plans;
    private final FeedingLogRepository logs;

    public LivestockManagementQueryServiceImpl(
            AnimalRepository animals,
            LotRepository lots,
            MovementRepository movements,
            FeedingPlanRepository plans,
            FeedingLogRepository logs
    ) {
        this.animals = animals;
        this.lots = lots;
        this.movements = movements;
        this.plans = plans;
        this.logs = logs;
    }

    @Override
    public List<Animal> handle(GetAllAnimalsQuery query) {
        return animals.findAll();
    }

    @Override
    public Optional<Animal> handle(GetAnimalByIdQuery query) {
        return animals.findById(query.id());
    }

    @Override
    public List<Lot> handle(GetAllLotsQuery query) {
        return lots.findAll();
    }

    @Override
    public Optional<Lot> handle(GetLotByIdQuery query) {
        return lots.findById(query.id());
    }

    @Override
    public List<Movement> handle(GetMovementsQuery query) {
        return query.animalId() == null
                ? movements.findAll()
                : movements.findAllByAnimalId(query.animalId());
    }

    @Override
    public List<FeedingPlan> handle(GetAllFeedingPlansQuery query) {
        return plans.findAll();
    }

    @Override
    public Optional<FeedingPlan> handle(GetFeedingPlanByIdQuery query) {
        return plans.findById(query.id());
    }

    @Override
    public List<FeedingPlanItem> handle(GetFeedingPlanItemsQuery query) {
        return plans.findById(query.feedingPlanId())
                .map(FeedingPlan::getItems)
                .orElse(List.of());
    }

    @Override
    public List<FeedingLog> handle(GetFeedingLogsQuery query) {

        if (query.lotId() != null && query.animalId() != null) {
            return logs.findAllByLotIdAndAnimalId(
                    query.lotId(),
                    query.animalId()
            );
        }

        if (query.animalId() != null) {
            return logs.findAllByAnimalId(query.animalId());
        }

        if (query.lotId() != null) {
            return logs.findAllByLotId(query.lotId());
        }

        return logs.findAll();
    }
}

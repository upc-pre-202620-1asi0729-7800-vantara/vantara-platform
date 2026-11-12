package com.vantara.platform.livestockmanagement.application.internal.queryservices;

import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.application.queries.*;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import com.vantara.platform.livestockmanagement.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LivestockManagementQueryServiceImpl implements LivestockManagementQueryService {
    private final AnimalRepository animals; private final LotRepository lots; private final MovementRepository movements;
    private final FeedingPlanRepository plans; private final FeedingPlanItemRepository planItems; private final FeedingLogRepository logs;
    public LivestockManagementQueryServiceImpl(AnimalRepository animals, LotRepository lots, MovementRepository movements, FeedingPlanRepository plans, FeedingPlanItemRepository planItems, FeedingLogRepository logs) { this.animals=animals; this.lots=lots; this.movements=movements; this.plans=plans; this.planItems=planItems; this.logs=logs; }
    public List<Animal> handle(GetAllAnimalsQuery query) { return animals.findAll(); }
    public Optional<Animal> handle(GetAnimalByIdQuery query) { return animals.findById(query.id()); }
    public List<Lot> handle(GetAllLotsQuery query) { return lots.findAll(); }
    public Optional<Lot> handle(GetLotByIdQuery query) { return lots.findById(query.id()); }
    public List<Movement> handle(GetMovementsQuery query) { return query.animalId() == null ? movements.findAll() : movements.findAllByAnimalId(query.animalId()); }
    public List<FeedingPlan> handle(GetAllFeedingPlansQuery query) { return plans.findAll(); }
    public Optional<FeedingPlan> handle(GetFeedingPlanByIdQuery query) { return plans.findById(query.id()); }
    public List<FeedingPlanItem> handle(GetFeedingPlanItemsQuery query) { return query.feedingPlanId() == null ? planItems.findAll() : planItems.findAllByFeedingPlanId(query.feedingPlanId()); }
    public List<FeedingLog> handle(GetFeedingLogsQuery query) { if (query.animalId() != null) return logs.findAllByAnimalId(query.animalId()); if (query.lotId() != null) return logs.findAllByLotId(query.lotId()); return logs.findAll(); }
}

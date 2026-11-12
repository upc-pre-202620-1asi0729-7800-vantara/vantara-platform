package com.vantara.platform.livestockmanagement.application.internal.queryservices;

import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
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
    public List<Animal> getAnimals() { return animals.findAll(); } public Optional<Animal> getAnimal(String id) { return animals.findById(id); }
    public List<Lot> getLots() { return lots.findAll(); } public Optional<Lot> getLot(String id) { return lots.findById(id); }
    public List<Movement> getMovements(String animalId) { return animalId == null || animalId.isBlank() ? movements.findAll() : movements.findAllByAnimalId(animalId); }
    public List<FeedingPlan> getFeedingPlans() { return plans.findAll(); } public Optional<FeedingPlan> getFeedingPlan(String id) { return plans.findById(id); }
    public List<FeedingPlanItem> getFeedingPlanItems(String feedingPlanId) { return feedingPlanId == null || feedingPlanId.isBlank() ? planItems.findAll() : planItems.findAllByFeedingPlanId(feedingPlanId); }
    public List<FeedingLog> getFeedingLogs(String lotId, String animalId) { if (animalId != null && !animalId.isBlank()) return logs.findAllByAnimalId(animalId); if (lotId != null && !lotId.isBlank()) return logs.findAllByLotId(lotId); return logs.findAll(); }
}

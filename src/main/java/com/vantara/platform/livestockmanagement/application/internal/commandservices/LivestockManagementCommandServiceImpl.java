package com.vantara.platform.livestockmanagement.application.internal.commandservices;

import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import com.vantara.platform.livestockmanagement.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LivestockManagementCommandServiceImpl implements LivestockManagementCommandService {
    private final AnimalRepository animals; private final LotRepository lots; private final MovementRepository movements;
    private final FeedingPlanRepository plans; private final FeedingPlanItemRepository planItems; private final FeedingLogRepository feedingLogs;
    public LivestockManagementCommandServiceImpl(AnimalRepository animals, LotRepository lots, MovementRepository movements, FeedingPlanRepository plans, FeedingPlanItemRepository planItems, FeedingLogRepository feedingLogs) { this.animals=animals; this.lots=lots; this.movements=movements; this.plans=plans; this.planItems=planItems; this.feedingLogs=feedingLogs; }
    public Animal createAnimal(Animal animal) {
        requireLot(animal.getLotId());
        if (animals.existsByEarTag(animal.getEarTag())) throw new IllegalArgumentException("An animal with this ear tag already exists");
        return animals.save(animal);
    }
    public Animal updateAnimal(String id, Animal replacement) {
        var current = animals.findById(id).orElseThrow(() -> new IllegalArgumentException("Animal not found: " + id));
        requireLot(replacement.getLotId());
        current.update(replacement.getLotId(), replacement.getName(), replacement.getSpecies(), replacement.getBreed(), replacement.getSex(), replacement.getBirthDate(), replacement.getMotherId(), replacement.getFatherId(), replacement.getWeight(), replacement.getStatus(), replacement.getPhotoUrl(), replacement.getWeanedOn());
        return animals.save(current);
    }
    public void deleteAnimal(String id) { if (animals.findById(id).isEmpty()) throw new IllegalArgumentException("Animal not found: " + id); animals.deleteById(id); }
    public Lot createLot(Lot lot) { return lots.save(lot); }
    public Movement recordMovement(Movement movement) {
        var animal = animals.findById(movement.getAnimalId()).orElseThrow(() -> new IllegalArgumentException("Animal not found: " + movement.getAnimalId()));
        requireLot(movement.getDestinationLotId());
        if (movement.getOriginLotId() != null && !movement.getOriginLotId().equals(animal.getLotId())) throw new IllegalArgumentException("Origin lot does not match the animal's current lot");
        animal.assignToLot(movement.getDestinationLotId()); animals.save(animal); return movements.save(movement);
    }
    public FeedingPlan createFeedingPlan(FeedingPlan plan) { requireLot(plan.getLotId()); if (plan.getAnimalId() != null && animals.findById(plan.getAnimalId()).isEmpty()) throw new IllegalArgumentException("Animal not found: " + plan.getAnimalId()); return plans.save(plan); }
    public FeedingPlanItem createFeedingPlanItem(FeedingPlanItem item) { if (plans.findById(item.getFeedingPlanId()).isEmpty()) throw new IllegalArgumentException("Feeding plan not found: " + item.getFeedingPlanId()); return planItems.save(item); }
    public FeedingLog recordFeeding(FeedingLog log) { requireLot(log.getLotId()); if (log.getAnimalId() != null && animals.findById(log.getAnimalId()).isEmpty()) throw new IllegalArgumentException("Animal not found: " + log.getAnimalId()); return feedingLogs.save(log); }
    private void requireLot(String lotId) { if (lots.findById(lotId).isEmpty()) throw new IllegalArgumentException("Lot not found: " + lotId); }
}

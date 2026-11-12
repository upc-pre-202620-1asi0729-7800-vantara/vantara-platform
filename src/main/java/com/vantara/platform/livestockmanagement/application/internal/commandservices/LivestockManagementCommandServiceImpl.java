package com.vantara.platform.livestockmanagement.application.internal.commandservices;

import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandFailure;
import com.vantara.platform.livestockmanagement.application.commands.*;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import com.vantara.platform.livestockmanagement.domain.repositories.*;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class LivestockManagementCommandServiceImpl implements LivestockManagementCommandService {
    private final AnimalRepository animals; private final LotRepository lots; private final MovementRepository movements;
    private final FeedingPlanRepository plans; private final FeedingPlanItemRepository planItems; private final FeedingLogRepository feedingLogs;
    public LivestockManagementCommandServiceImpl(AnimalRepository animals, LotRepository lots, MovementRepository movements, FeedingPlanRepository plans, FeedingPlanItemRepository planItems, FeedingLogRepository feedingLogs) { this.animals=animals; this.lots=lots; this.movements=movements; this.plans=plans; this.planItems=planItems; this.feedingLogs=feedingLogs; }
    public Result<Animal, LivestockManagementCommandFailure> handle(CreateAnimalCommand command) {
        if (lots.findById(command.lotId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.LotNotFound(command.lotId()));
        if (animals.existsByEarTag(command.earTag())) return Result.failure(new LivestockManagementCommandFailure.DuplicateEarTag());
        var animal = Animal.register(id(), command.rancherId(), command.lotId(), command.earTag(), command.name(), command.species(), command.breed(), command.sex(), command.birthDate(), command.motherId(), command.fatherId(), command.weight(), command.status(), command.registeredAt(), command.photoUrl(), command.weanedOn());
        return Result.success(animals.save(animal));
    }
    public Result<Animal, LivestockManagementCommandFailure> handle(UpdateAnimalCommand command) {
        var current = animals.findById(command.id());
        if (current.isEmpty()) return Result.failure(new LivestockManagementCommandFailure.AnimalNotFound(command.id()));
        if (lots.findById(command.lotId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.LotNotFound(command.lotId()));
        var animal = current.orElseThrow();
        animal.update(command.lotId(), command.name(), command.species(), command.breed(), command.sex(), command.birthDate(), command.motherId(), command.fatherId(), command.weight(), command.status(), command.photoUrl(), command.weanedOn());
        return Result.success(animals.save(animal));
    }
    public Result<Boolean, LivestockManagementCommandFailure> handle(DeleteAnimalCommand command) {
        if (animals.findById(command.id()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.AnimalNotFound(command.id()));
        animals.deleteById(command.id()); return Result.success(Boolean.TRUE);
    }
    public Result<Lot, LivestockManagementCommandFailure> handle(CreateLotCommand command) {
        return Result.success(lots.save(Lot.register(id(), command.rancherId(), command.name(), command.purpose(), command.status())));
    }
    public Result<Movement, LivestockManagementCommandFailure> handle(RecordMovementCommand command) {
        var foundAnimal = animals.findById(command.animalId());
        if (foundAnimal.isEmpty()) return Result.failure(new LivestockManagementCommandFailure.AnimalNotFound(command.animalId()));
        if (lots.findById(command.destinationLotId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.LotNotFound(command.destinationLotId()));
        var animal = foundAnimal.orElseThrow();
        if (command.originLotId() != null && !command.originLotId().isBlank() && !command.originLotId().equals(animal.getLotId())) return Result.failure(new LivestockManagementCommandFailure.InvalidRelationship("Origin lot does not match the animal's current lot"));
        var movement = Movement.record(id(), command.animalId(), command.originLotId(), command.destinationLotId(), command.movementType(), command.occurredAt(), command.reason(), command.recordedBy());
        animal.assignToLot(command.destinationLotId()); animals.save(animal);
        return Result.success(movements.save(movement));
    }
    public Result<FeedingPlan, LivestockManagementCommandFailure> handle(CreateFeedingPlanCommand command) {
        if (lots.findById(command.lotId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.LotNotFound(command.lotId()));
        if (command.animalId() != null && !command.animalId().isBlank() && animals.findById(command.animalId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.AnimalNotFound(command.animalId()));
        var plan = FeedingPlan.create(id(), command.lotId(), command.animalId(), command.name(), command.startsOn(), command.endsOn(), command.objective(), command.currentAverageWeight(), command.targetWeight(), command.frequency(), command.daysOfWeek(), command.schedules(), command.instructions(), command.status());
        return Result.success(plans.save(plan));
    }
    public Result<FeedingPlanItem, LivestockManagementCommandFailure> handle(CreateFeedingPlanItemCommand command) {
        if (plans.findById(command.feedingPlanId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.FeedingPlanNotFound(command.feedingPlanId()));
        return Result.success(planItems.save(FeedingPlanItem.create(id(), command.feedingPlanId(), command.feedName(), command.dailyQuantity(), command.unit())));
    }
    public Result<FeedingLog, LivestockManagementCommandFailure> handle(RecordFeedingCommand command) {
        if (lots.findById(command.lotId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.LotNotFound(command.lotId()));
        if (command.animalId() != null && !command.animalId().isBlank() && animals.findById(command.animalId()).isEmpty()) return Result.failure(new LivestockManagementCommandFailure.AnimalNotFound(command.animalId()));
        var log = FeedingLog.record(id(), command.planItemId(), command.animalId(), command.lotId(), command.feedName(), command.feedAt(), command.quantity(), command.unit(), command.recordedBy(), command.notes());
        return Result.success(feedingLogs.save(log));
    }
    private String id() { return UUID.randomUUID().toString(); }
}

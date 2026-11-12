
package com.vantara.platform.livestockmanagement.application.internal.commandservices;

import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandFailure;
import com.vantara.platform.livestockmanagement.application.commands.*;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.AnimalWeight;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.EarTag;
import com.vantara.platform.livestockmanagement.domain.repositories.*;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class LivestockManagementCommandServiceImpl
        implements LivestockManagementCommandService {

    private final AnimalRepository animals;
    private final LotRepository lots;
    private final MovementRepository movements;
    private final FeedingPlanRepository plans;
    private final FeedingLogRepository feedingLogs;

    public LivestockManagementCommandServiceImpl(
            AnimalRepository animals,
            LotRepository lots,
            MovementRepository movements,
            FeedingPlanRepository plans,
            FeedingLogRepository feedingLogs
    ) {
        this.animals = animals;
        this.lots = lots;
        this.movements = movements;
        this.plans = plans;
        this.feedingLogs = feedingLogs;
    }


    public Result<Animal, LivestockManagementCommandFailure> handle(
            CreateAnimalCommand command
    ) {
        if (lots.findByIdAndRancherId(
                command.lotId(),
                command.rancherId()
        ).isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.LotNotFound(
                            command.lotId()
                    )
            );
        }

        if (animals.existsByEarTagAndRancherId(
                command.earTag(),
                command.rancherId()
        )) {
            return Result.failure(
                    new LivestockManagementCommandFailure.DuplicateEarTag()
            );
        }

        var animal = Animal.register(
                id(),
                command.rancherId(),
                command.lotId(),
                new EarTag(command.earTag()),
                command.name(),
                command.species(),
                command.breed(),
                command.sex(),
                command.birthDate(),
                command.motherId(),
                command.fatherId(),
                new AnimalWeight(command.weight()),
                command.status(),
                command.registeredAt(),
                command.photoUrl(),
                command.weanedOn()
        );

        return Result.success(animals.save(animal));
    }

    public Result<Animal, LivestockManagementCommandFailure> handle(
            UpdateAnimalCommand command
    ) {
        var current = animals.findById(command.id());

        if (current.isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.AnimalNotFound(
                            command.id()
                    )
            );
        }

        var animal = current.orElseThrow();

        if (lots.findByIdAndRancherId(
                command.lotId(),
                animal.getRancherId()
        ).isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.LotNotFound(
                            command.lotId()
                    )
            );
        }

        animal.update(
                command.lotId(),
                command.name(),
                command.species(),
                command.breed(),
                command.sex(),
                command.birthDate(),
                command.motherId(),
                command.fatherId(),
                new AnimalWeight(command.weight()),
                command.status(),
                command.photoUrl(),
                command.weanedOn()
        );

        return Result.success(animals.save(animal));
    }

    public Result<Boolean, LivestockManagementCommandFailure> handle(
            DeleteAnimalCommand command
    ) {
        if (animals.findById(command.id()).isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.AnimalNotFound(
                            command.id()
                    )
            );
        }

        animals.deleteById(command.id());

        return Result.success(Boolean.TRUE);
    }



    public Result<Lot, LivestockManagementCommandFailure> handle(
            CreateLotCommand command
    ) {
        var lot = Lot.register(
                id(),
                command.rancherId(),
                command.name(),
                command.purpose(),
                command.status()
        );

        return Result.success(lots.save(lot));
    }

    public Result<Movement, LivestockManagementCommandFailure> handle(
            RecordMovementCommand command
    ) {
        var foundAnimal = animals.findById(command.animalId());

        if (foundAnimal.isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.AnimalNotFound(
                            command.animalId()
                    )
            );
        }

        var animal = foundAnimal.orElseThrow();

        if (lots.findByIdAndRancherId(
                command.destinationLotId(),
                animal.getRancherId()
        ).isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.LotNotFound(
                            command.destinationLotId()
                    )
            );
        }

        if (command.originLotId() != null
                && !command.originLotId().isBlank()
                && !command.originLotId().equals(animal.getLotId())) {
            return Result.failure(
                    new LivestockManagementCommandFailure.InvalidRelationship(
                            "Origin lot does not match the animal's current lot"
                    )
            );
        }

        var movement = Movement.record(
                id(),
                command.animalId(),
                command.originLotId(),
                command.destinationLotId(),
                command.movementType(),
                command.occurredAt(),
                command.reason(),
                command.recordedBy()
        );

        animal.assignToLot(command.destinationLotId());
        animals.save(animal);

        return Result.success(movements.save(movement));
    }

    public Result<FeedingPlan, LivestockManagementCommandFailure> handle(
            CreateFeedingPlanCommand command
    ) {
        var foundLot = lots.findById(command.lotId());

        if (foundLot.isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.LotNotFound(
                            command.lotId()
                    )
            );
        }

        if (command.animalId() != null
                && !command.animalId().isBlank()) {

            var foundAnimal = animals.findById(command.animalId());

            if (foundAnimal.isEmpty()) {
                return Result.failure(
                        new LivestockManagementCommandFailure.AnimalNotFound(
                                command.animalId()
                        )
                );
            }

            if (!foundAnimal.orElseThrow().getLotId().equals(command.lotId())) {
                return Result.failure(
                        new LivestockManagementCommandFailure.InvalidRelationship(
                                "The animal does not belong to the specified lot"
                        )
                );
            }
        }

        var planId = id();

        var schedules = command.schedules().stream()
                .map(schedule -> new FeedingPlan.Schedule(
                        schedule.time(),
                        schedule.quantityPerAnimal()
                ))
                .toList();

        var items = command.items().stream()
                .map(item -> FeedingPlanItem.create(
                        id(),
                        planId,
                        item.feedName(),
                        item.dailyQuantity(),
                        item.unit()
                ))
                .toList();

        var plan = FeedingPlan.create(
                planId,
                command.lotId(),
                command.animalId(),
                command.name(),
                command.startsOn(),
                command.endsOn(),
                command.objective(),
                command.currentAverageWeight(),
                command.targetWeight(),
                command.frequency(),
                command.daysOfWeek(),
                schedules,
                command.instructions(),
                command.status(),
                items
        );

        return Result.success(plans.save(plan));
    }

    public Result<FeedingLog, LivestockManagementCommandFailure> handle(
            RecordFeedingCommand command
    ) {
        // 1. Buscar el plan de alimentación.
        var foundPlan = plans.findById(command.feedingPlanId());

        if (foundPlan.isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.FeedingPlanNotFound(
                            command.feedingPlanId()
                    )
            );
        }

        var plan = foundPlan.orElseThrow();

        // 2. Comprobar que el plan pertenece al lote indicado.
        if (!plan.getLotId().equals(command.lotId())) {
            return Result.failure(
                    new LivestockManagementCommandFailure.InvalidRelationship(
                            "The feeding plan does not belong to the specified lot"
                    )
            );
        }

        // 3. Buscar el ingrediente dentro del agregado FeedingPlan.
        var foundItem = plan.getItems().stream()
                .filter(item -> item.getId().equals(command.planItemId()))
                .findFirst();

        if (foundItem.isEmpty()) {
            return Result.failure(
                    new LivestockManagementCommandFailure.InvalidRelationship(
                            "The feeding item does not belong to the specified feeding plan"
                    )
            );
        }

        var item = foundItem.orElseThrow();

        // 4. Comprobar el animal, si el registro es individual.
        if (command.animalId() != null && !command.animalId().isBlank()) {
            var foundAnimal = animals.findById(command.animalId());

            if (foundAnimal.isEmpty()) {
                return Result.failure(
                        new LivestockManagementCommandFailure.AnimalNotFound(
                                command.animalId()
                        )
                );
            }

            if (!foundAnimal.orElseThrow().getLotId().equals(command.lotId())) {
                return Result.failure(
                        new LivestockManagementCommandFailure.InvalidRelationship(
                                "The animal does not belong to the specified lot"
                        )
                );
            }
        }

        // 5. Registrar utilizando los datos originales del ingrediente.
        var log = FeedingLog.record(
                id(),
                command.planItemId(),
                command.animalId(),
                command.lotId(),
                item.getFeedName(),
                command.feedAt(),
                command.quantity(),
                item.getUnit(),
                command.recordedBy(),
                command.notes()
        );

        return Result.success(feedingLogs.save(log));
    }


    private String id() {
        return UUID.randomUUID().toString();
    }
}

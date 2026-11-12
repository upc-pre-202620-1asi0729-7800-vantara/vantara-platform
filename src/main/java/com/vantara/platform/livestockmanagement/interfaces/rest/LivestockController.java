
package com.vantara.platform.livestockmanagement.interfaces.rest;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.vantara.platform.livestockmanagement.application.commands.*;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.queries.*;
import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.interfaces.rest.resources.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class LivestockController {

    private final LivestockManagementCommandService commands;
    private final LivestockManagementQueryService queries;
    private final ObjectMapper objectMapper;

    public LivestockController(
            LivestockManagementCommandService commands,
            LivestockManagementQueryService queries,
            ObjectMapper objectMapper
    ) {
        this.commands = commands;
        this.queries = queries;
        this.objectMapper = objectMapper;
    }

    // ANIMALS

    @GetMapping("/animals")
    public java.util.List<AnimalResource> getAnimals() {
        return queries.handle(new GetAllAnimalsQuery())
                .stream()
                .map(AnimalResource::from)
                .toList();
    }

    @GetMapping("/animals/{id}")
    public ResponseEntity<AnimalResource> getAnimal(
            @PathVariable String id
    ) {
        return queries.handle(new GetAnimalByIdQuery(id))
                .map(AnimalResource::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/animals")
    public ResponseEntity<AnimalResource> createAnimal(
            @Valid @RequestBody AnimalResource resource
    ) {
        return commands.handle(
                        new CreateAnimalCommand(
                                resource.rancherId(),
                                resource.lotId(),
                                resource.earTag(),
                                resource.name(),
                                resource.species(),
                                resource.breed(),
                                resource.sex(),
                                resource.birthDate(),
                                resource.motherId(),
                                resource.fatherId(),
                                resource.weight(),
                                resource.status(),
                                resource.registeredAt(),
                                resource.photoUrl(),
                                resource.weanedOn()
                        )
                ).toOptional()
                .map(AnimalResource::from)
                .map(body -> ResponseEntity.status(201).body(body))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PatchMapping("/animals/{id}")
    public ResponseEntity<AnimalResource> updateAnimal(
            @PathVariable String id,
            @RequestBody Map<String, Object> changes
    ) {
        var existing = queries.handle(new GetAnimalByIdQuery(id));

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> fields = new LinkedHashMap<>(
                objectMapper.convertValue(
                        AnimalResource.from(existing.orElseThrow()),
                        new TypeReference<>() {
                        }
                )
        );

        changes.forEach((key, value) -> {
            if (!key.equals("id")
                    && !key.equals("rancherId")
                    && !key.equals("earTag")) {
                fields.put(key, value);
            }
        });

        var replacement = objectMapper.convertValue(
                fields,
                AnimalResource.class
        );

        return commands.handle(
                        new UpdateAnimalCommand(
                                id,
                                replacement.lotId(),
                                replacement.name(),
                                replacement.species(),
                                replacement.breed(),
                                replacement.sex(),
                                replacement.birthDate(),
                                replacement.motherId(),
                                replacement.fatherId(),
                                replacement.weight(),
                                replacement.status(),
                                replacement.photoUrl(),
                                replacement.weanedOn()
                        )
                ).toOptional()
                .map(AnimalResource::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/animals/{id}")
    public ResponseEntity<Void> deleteAnimal(
            @PathVariable String id
    ) {
        return commands.handle(new DeleteAnimalCommand(id))
                .toOptional()
                .map(ignored -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // LOTS

    @GetMapping("/lots")
    public java.util.List<LotResource> getLots() {
        return queries.handle(new GetAllLotsQuery())
                .stream()
                .map(LotResource::from)
                .toList();
    }

    @GetMapping("/lots/{id}")
    public ResponseEntity<LotResource> getLot(
            @PathVariable String id
    ) {
        return queries.handle(new GetLotByIdQuery(id))
                .map(LotResource::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/lots")
    public ResponseEntity<LotResource> createLot(
            @Valid @RequestBody LotResource resource
    ) {
        return commands.handle(
                        new CreateLotCommand(
                                resource.rancherId(),
                                resource.name(),
                                resource.purpose(),
                                resource.status()
                        )
                ).toOptional()
                .map(LotResource::from)
                .map(body -> ResponseEntity.status(201).body(body))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // MOVEMENTS

    @GetMapping("/movements")
    public java.util.List<MovementResource> getMovements(
            @RequestParam(required = false) String animalId
    ) {
        return queries.handle(new GetMovementsQuery(animalId))
                .stream()
                .map(MovementResource::from)
                .toList();
    }

    @PostMapping("/movements")
    public ResponseEntity<MovementResource> recordMovement(
            @Valid @RequestBody MovementResource resource
    ) {
        return commands.handle(
                        new RecordMovementCommand(
                                resource.animalId(),
                                resource.originLotId(),
                                resource.destinationLotId(),
                                resource.movementType(),
                                resource.occurredAt(),
                                resource.reason(),
                                resource.recordedBy()
                        )
                ).toOptional()
                .map(MovementResource::from)
                .map(body -> ResponseEntity.status(201).body(body))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // FEEDING PLANS

    @GetMapping("/feedingPlans")
    public java.util.List<FeedingPlanResource> getFeedingPlans() {
        return queries.handle(new GetAllFeedingPlansQuery())
                .stream()
                .map(FeedingPlanResource::from)
                .toList();
    }

    @GetMapping("/feedingPlans/{id}")
    public ResponseEntity<FeedingPlanResource> getFeedingPlan(
            @PathVariable String id
    ) {
        return queries.handle(new GetFeedingPlanByIdQuery(id))
                .map(FeedingPlanResource::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/feedingPlans")
    public ResponseEntity<FeedingPlanResource> createFeedingPlan(
            @Valid @RequestBody FeedingPlanResource resource
    ) {
        return commands.handle(
                        new CreateFeedingPlanCommand(
                                resource.lotId(),
                                resource.animalId(),
                                resource.name(),
                                resource.startsOn(),
                                resource.endsOn(),
                                resource.objective(),
                                resource.currentAverageWeight(),
                                resource.targetWeight(),
                                resource.frequency(),
                                resource.daysOfWeek(),

                                resource.schedules().stream()
                                        .map(schedule ->
                                                new CreateFeedingPlanCommand.ScheduleData(
                                                        schedule.time(),
                                                        schedule.quantityPerAnimal()
                                                )
                                        )
                                        .toList(),

                                resource.instructions(),
                                resource.status(),

                                resource.items().stream()
                                        .map(item ->
                                                new CreateFeedingPlanCommand.ItemData(
                                                        item.feedName(),
                                                        item.dailyQuantity(),
                                                        item.unit()
                                                )
                                        )
                                        .toList()
                        )
                ).toOptional()
                .map(FeedingPlanResource::from)
                .map(body -> ResponseEntity.status(201).body(body))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // FEEDING LOGS

    @GetMapping("/feedingLogs")
    public java.util.List<FeedingLogResource> getFeedingLogs(
            @RequestParam(required = false) String lotId,
            @RequestParam(required = false) String animalId
    ) {
        return queries.handle(new GetFeedingLogsQuery(lotId, animalId))
                .stream()
                .map(FeedingLogResource::from)
                .toList();
    }

    @PostMapping("/feedingLogs")
    public ResponseEntity<FeedingLogResource> recordFeeding(
            @Valid @RequestBody RecordFeedingResource resource
    ) {
        return commands.handle(
                        new RecordFeedingCommand(
                                resource.feedingPlanId(),
                                resource.planItemId(),
                                resource.animalId(),
                                resource.lotId(),
                                resource.feedAt(),
                                resource.quantity(),
                                resource.recordedBy(),
                                resource.notes()
                        )
                ).toOptional()
                .map(FeedingLogResource::from)
                .map(body -> ResponseEntity.status(201).body(body))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}

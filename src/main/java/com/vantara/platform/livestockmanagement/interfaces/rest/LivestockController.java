
package com.vantara.platform.livestockmanagement.interfaces.rest;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.vantara.platform.livestockmanagement.application.commands.*;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.queries.*;
import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.interfaces.rest.resources.*;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandFailure;
import com.vantara.platform.livestockmanagement.interfaces.rest.transform.LivestockFailureAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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
    public ResponseEntity<?> getAnimal(
            @PathVariable String id
    ) {
        return queries.handle(new GetAnimalByIdQuery(id))
                .map(AnimalResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("animal", id));
    }

    @PostMapping("/animals")
    public ResponseEntity<?> createAnimal(
            @Valid @RequestBody AnimalResource resource
    ) {
        return respond(commands.handle(
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
                ), AnimalResource::from, HttpStatus.CREATED);
    }

    @PatchMapping("/animals/{id}")
    public ResponseEntity<?> updateAnimal(
            @PathVariable String id,
            @RequestBody Map<String, Object> changes
    ) {
        var existing = queries.handle(new GetAnimalByIdQuery(id));

        if (existing.isEmpty()) {
            return notFound("animal", id);
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

        return respond(commands.handle(
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
                ), AnimalResource::from, HttpStatus.OK);
    }

    @DeleteMapping("/animals/{id}")
    public ResponseEntity<?> deleteAnimal(
            @PathVariable String id
    ) {
        return respond(commands.handle(new DeleteAnimalCommand(id)), ignored -> null, HttpStatus.NO_CONTENT);
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
    public ResponseEntity<?> getLot(
            @PathVariable String id
    ) {
        return queries.handle(new GetLotByIdQuery(id))
                .map(LotResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("lot", id));
    }

    @PostMapping("/lots")
    public ResponseEntity<?> createLot(
            @Valid @RequestBody LotResource resource
    ) {
        return respond(commands.handle(
                        new CreateLotCommand(
                                resource.rancherId(),
                                resource.name(),
                                resource.purpose(),
                                resource.status()
                        )
                ), LotResource::from, HttpStatus.CREATED);
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
    public ResponseEntity<?> recordMovement(
            @Valid @RequestBody MovementResource resource
    ) {
        return respond(commands.handle(
                        new RecordMovementCommand(
                                resource.animalId(),
                                resource.originLotId(),
                                resource.destinationLotId(),
                                resource.movementType(),
                                resource.occurredAt(),
                                resource.reason(),
                                resource.recordedBy()
                        )
                ), MovementResource::from, HttpStatus.CREATED);
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
    public ResponseEntity<?> getFeedingPlan(
            @PathVariable String id
    ) {
        return queries.handle(new GetFeedingPlanByIdQuery(id))
                .map(FeedingPlanResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("feedingPlan", id));
    }

    @PostMapping("/feedingPlans")
    public ResponseEntity<?> createFeedingPlan(
            @Valid @RequestBody FeedingPlanResource resource
    ) {
        return respond(commands.handle(
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
                ), FeedingPlanResource::from, HttpStatus.CREATED);
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
    public ResponseEntity<?> recordFeeding(
            @Valid @RequestBody RecordFeedingResource resource
    ) {
        return respond(commands.handle(
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
                ), FeedingLogResource::from, HttpStatus.CREATED);
    }

    private static <T, R> ResponseEntity<?> respond(
            Result<T, LivestockManagementCommandFailure> result,
            Function<T, R> resourceAssembler,
            HttpStatus status
    ) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(LivestockFailureAssembler::toApplicationError),
                resourceAssembler,
                status
        );
    }

    private static ResponseEntity<?> notFound(String resource, String id) {
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.notFound(resource, id)
        );
    }
}

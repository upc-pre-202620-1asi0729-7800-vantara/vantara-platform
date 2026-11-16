
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
import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
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
import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class LivestockController {

    private final LivestockManagementCommandService commands;
    private final LivestockManagementQueryService queries;
    private final ObjectMapper objectMapper;
    private final AuthenticatedUserIdentity identity;
    private final LivestockContextFacade livestock;

    public LivestockController(
            LivestockManagementCommandService commands,
            LivestockManagementQueryService queries,
            ObjectMapper objectMapper,
            AuthenticatedUserIdentity identity,
            LivestockContextFacade livestock
    ) {
        this.commands = commands;
        this.queries = queries;
        this.objectMapper = objectMapper;
        this.identity = identity;
        this.livestock = livestock;
    }

    // ANIMALS

    @GetMapping("/animals")
    public ResponseEntity<?> getAnimals(Principal principal) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        return ResponseEntity.ok(queries.handle(new GetAllAnimalsQuery())
                .stream()
                .filter(animal -> owner.get().equals(animal.getRancherId()))
                .map(AnimalResource::from)
                .toList());
    }

    @GetMapping("/animals/{id}")
    public ResponseEntity<?> getAnimal(
            @PathVariable String id,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        return queries.handle(new GetAnimalByIdQuery(id))
                .filter(animal -> owner.get().equals(animal.getRancherId()))
                .map(AnimalResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("animal", id));
    }

    @PostMapping("/animals")
    public ResponseEntity<?> createAnimal(
            @Valid @RequestBody AnimalResource resource,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        if (!ownsOptionalParent(owner.get(), resource.motherId()) || !ownsOptionalParent(owner.get(), resource.fatherId())) {
            return notFound("animal", resource.motherId() != null ? resource.motherId() : resource.fatherId());
        }
        return respond(commands.handle(
                        new CreateAnimalCommand(
                                owner.get(),
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
            @RequestBody Map<String, Object> changes,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var existing = queries.handle(new GetAnimalByIdQuery(id));

        if (existing.isEmpty() || !owner.get().equals(existing.orElseThrow().getRancherId())) {
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
        if (!ownsOptionalParent(owner.get(), replacement.motherId()) || !ownsOptionalParent(owner.get(), replacement.fatherId())) {
            return notFound("animal", replacement.motherId() != null ? replacement.motherId() : replacement.fatherId());
        }

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
            @PathVariable String id,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var animal = queries.handle(new GetAnimalByIdQuery(id));
        if (animal.isEmpty() || !owner.get().equals(animal.orElseThrow().getRancherId())) return notFound("animal", id);
        return respond(commands.handle(new DeleteAnimalCommand(id)), ignored -> null, HttpStatus.NO_CONTENT);
    }

    // LOTS

    @GetMapping("/lots")
    public ResponseEntity<?> getLots(Principal principal) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        return ResponseEntity.ok(queries.handle(new GetAllLotsQuery())
                .stream()
                .filter(lot -> owner.get().equals(lot.getRancherId()))
                .map(LotResource::from)
                .toList());
    }

    @GetMapping("/lots/{id}")
    public ResponseEntity<?> getLot(
            @PathVariable String id,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        return queries.handle(new GetLotByIdQuery(id))
                .filter(lot -> owner.get().equals(lot.getRancherId()))
                .map(LotResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("lot", id));
    }

    @PostMapping("/lots")
    public ResponseEntity<?> createLot(
            @Valid @RequestBody LotResource resource,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        return respond(commands.handle(
                        new CreateLotCommand(
                                owner.get(),
                                resource.name(),
                                resource.purpose(),
                                resource.status()
                        )
                ), LotResource::from, HttpStatus.CREATED);
    }

    // MOVEMENTS

    @GetMapping("/movements")
    public ResponseEntity<?> getMovements(
            @RequestParam(required = false) String animalId,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var animalIds = livestock.listAnimalsOf(owner.get()).stream().map(a -> a.id()).collect(Collectors.toSet());
        var movements = queries.handle(new GetMovementsQuery(animalId)).stream()
                .filter(movement -> animalIds.contains(movement.getAnimalId()))
                .map(MovementResource::from)
                .toList();
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/movements")
    public ResponseEntity<?> recordMovement(
            @Valid @RequestBody MovementResource resource,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var ownedAnimalIds = livestock.listAnimalsOf(owner.get()).stream().map(a -> a.id()).collect(Collectors.toSet());
        if (!ownedAnimalIds.contains(resource.animalId())) return notFound("animal", resource.animalId());
        var ownedLotIds = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        if (!ownedLotIds.contains(resource.destinationLotId()) || (resource.originLotId() != null && !ownedLotIds.contains(resource.originLotId()))) return notFound("lot", resource.destinationLotId());
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
    public ResponseEntity<?> getFeedingPlans(Principal principal) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var lotIds = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        return ResponseEntity.ok(queries.handle(new GetAllFeedingPlansQuery()).stream()
                .filter(plan -> lotIds.contains(plan.getLotId()))
                .map(FeedingPlanResource::from)
                .toList());
    }

    @GetMapping("/feedingPlans/{id}")
    public ResponseEntity<?> getFeedingPlan(
            @PathVariable String id,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var ownedLotIds = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        return queries.handle(new GetFeedingPlanByIdQuery(id))
                .filter(plan -> ownedLotIds.contains(plan.getLotId()))
                .map(FeedingPlanResource::from)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound("feedingPlan", id));
    }

    @PostMapping("/feedingPlans")
    public ResponseEntity<?> createFeedingPlan(
            @Valid @RequestBody FeedingPlanResource resource,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var ownedLotIds = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        if (!ownedLotIds.contains(resource.lotId())) return notFound("lot", resource.lotId());
        if (resource.animalId() != null && !livestock.listAnimalsOf(owner.get()).stream().anyMatch(a -> a.id().equals(resource.animalId()))) return notFound("animal", resource.animalId());
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
    public ResponseEntity<?> getFeedingLogs(
            @RequestParam(required = false) String lotId,
            @RequestParam(required = false) String animalId,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var ownedLots = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        return ResponseEntity.ok(queries.handle(new GetFeedingLogsQuery(lotId, animalId)).stream()
                .filter(log -> ownedLots.contains(log.getLotId()))
                .map(FeedingLogResource::from)
                .toList());
    }

    @PostMapping("/feedingLogs")
    public ResponseEntity<?> recordFeeding(
            @Valid @RequestBody RecordFeedingResource resource,
            Principal principal
    ) {
        var owner = identity.rancherId(principal);
        if (owner.isEmpty()) return unauthorized();
        var ownedPlanIds = livestock.listFeedingPlansOf(owner.get()).stream().map(p -> p.id()).collect(Collectors.toSet());
        if (!ownedPlanIds.contains(resource.feedingPlanId())) return notFound("feedingPlan", resource.feedingPlanId());
        var itemBelongs = livestock.listFeedingPlanItemsOf(owner.get()).stream().anyMatch(i -> i.id().equals(resource.planItemId()) && i.feedingPlanId().equals(resource.feedingPlanId()));
        if (!itemBelongs) return notFound("feedingPlanItem", resource.planItemId());
        var ownedLots = livestock.listLotsOf(owner.get()).stream().map(l -> l.id()).collect(Collectors.toSet());
        if (!ownedLots.contains(resource.lotId())) return notFound("lot", resource.lotId());
        if (resource.animalId() != null && !livestock.listAnimalsOf(owner.get()).stream().anyMatch(a -> a.id().equals(resource.animalId()))) return notFound("animal", resource.animalId());
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

    private static ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean ownsOptionalParent(String ownerId, String parentId) {
        return parentId == null || parentId.isBlank()
                || livestock.listAnimalsOf(ownerId).stream().anyMatch(animal -> animal.id().equals(parentId));
    }
}

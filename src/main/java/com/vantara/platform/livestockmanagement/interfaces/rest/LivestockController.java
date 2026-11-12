package com.vantara.platform.livestockmanagement.interfaces.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.interfaces.rest.resources.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** REST adapter for the livestock-management bounded context. */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class LivestockController {
    private final LivestockManagementCommandService commands;
    private final LivestockManagementQueryService queries;
    private final ObjectMapper objectMapper;
    public LivestockController(LivestockManagementCommandService commands, LivestockManagementQueryService queries, ObjectMapper objectMapper) { this.commands = commands; this.queries = queries; this.objectMapper = objectMapper; }

    @GetMapping("/animals") public java.util.List<AnimalResource> getAnimals() { return queries.getAnimals().stream().map(AnimalResource::from).toList(); }
    @GetMapping("/animals/{id}") public ResponseEntity<AnimalResource> getAnimal(@PathVariable String id) { return queries.getAnimal(id).map(AnimalResource::from).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping("/animals") public ResponseEntity<AnimalResource> createAnimal(@Valid @RequestBody AnimalResource resource) { return ResponseEntity.status(201).body(AnimalResource.from(commands.createAnimal(resource.toDomain(UUID.randomUUID().toString())))); }
    @PatchMapping("/animals/{id}") public ResponseEntity<AnimalResource> updateAnimal(@PathVariable String id, @RequestBody Map<String, Object> changes) {
        var existing = queries.getAnimal(id); if (existing.isEmpty()) return ResponseEntity.notFound().build();
        Map<String, Object> fields = new LinkedHashMap<>(objectMapper.convertValue(AnimalResource.from(existing.orElseThrow()), new TypeReference<>() { }));
        changes.forEach((key, value) -> { if (!key.equals("id") && !key.equals("rancherId") && !key.equals("earTag")) fields.put(key, value); });
        var replacement = objectMapper.convertValue(fields, AnimalResource.class);
        return ResponseEntity.ok(AnimalResource.from(commands.updateAnimal(id, replacement.toDomain(id))));
    }
    @DeleteMapping("/animals/{id}") public ResponseEntity<Void> deleteAnimal(@PathVariable String id) { commands.deleteAnimal(id); return ResponseEntity.noContent().build(); }

    @GetMapping("/lots") public java.util.List<LotResource> getLots() { return queries.getLots().stream().map(LotResource::from).toList(); }
    @GetMapping("/lots/{id}") public ResponseEntity<LotResource> getLot(@PathVariable String id) { return queries.getLot(id).map(LotResource::from).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping("/lots") public ResponseEntity<LotResource> createLot(@Valid @RequestBody LotResource resource) { return ResponseEntity.status(201).body(LotResource.from(commands.createLot(resource.toDomain(UUID.randomUUID().toString())))); }

    @GetMapping("/movements") public java.util.List<MovementResource> getMovements(@RequestParam(required = false) String animalId) { return queries.getMovements(animalId).stream().map(MovementResource::from).toList(); }
    @PostMapping("/movements") public ResponseEntity<MovementResource> recordMovement(@Valid @RequestBody MovementResource resource) { return ResponseEntity.status(201).body(MovementResource.from(commands.recordMovement(resource.toDomain(UUID.randomUUID().toString())))); }

    @GetMapping("/feedingPlans") public java.util.List<FeedingPlanResource> getFeedingPlans() { return queries.getFeedingPlans().stream().map(FeedingPlanResource::from).toList(); }
    @GetMapping("/feedingPlans/{id}") public ResponseEntity<FeedingPlanResource> getFeedingPlan(@PathVariable String id) { return queries.getFeedingPlan(id).map(FeedingPlanResource::from).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping("/feedingPlans") public ResponseEntity<FeedingPlanResource> createFeedingPlan(@Valid @RequestBody FeedingPlanResource resource) { return ResponseEntity.status(201).body(FeedingPlanResource.from(commands.createFeedingPlan(resource.toDomain(UUID.randomUUID().toString())))); }

    @GetMapping("/feedingPlanItems") public java.util.List<FeedingPlanItemResource> getFeedingPlanItems(@RequestParam(required = false) String feedingPlanId) { return queries.getFeedingPlanItems(feedingPlanId).stream().map(FeedingPlanItemResource::from).toList(); }
    @PostMapping("/feedingPlanItems") public ResponseEntity<FeedingPlanItemResource> createFeedingPlanItem(@Valid @RequestBody FeedingPlanItemResource resource) { return ResponseEntity.status(201).body(FeedingPlanItemResource.from(commands.createFeedingPlanItem(resource.toDomain(UUID.randomUUID().toString())))); }

    @GetMapping("/feedingLogs") public java.util.List<FeedingLogResource> getFeedingLogs(@RequestParam(required = false) String lotId, @RequestParam(required = false) String animalId) { return queries.getFeedingLogs(lotId, animalId).stream().map(FeedingLogResource::from).toList(); }
    @PostMapping("/feedingLogs") public ResponseEntity<FeedingLogResource> recordFeeding(@Valid @RequestBody FeedingLogResource resource) { return ResponseEntity.status(201).body(FeedingLogResource.from(commands.recordFeeding(resource.toDomain(UUID.randomUUID().toString())))); }
}

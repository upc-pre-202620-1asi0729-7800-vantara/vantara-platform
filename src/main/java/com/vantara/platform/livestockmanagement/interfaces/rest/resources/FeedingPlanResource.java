package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FeedingPlanResource(String id, @NotBlank String lotId, String animalId, @NotBlank String name,
        @NotNull LocalDate startsOn, @NotNull LocalDate endsOn, @NotBlank String objective,
        @NotNull @DecimalMin("0") BigDecimal currentAverageWeight, @NotNull @DecimalMin("0") BigDecimal targetWeight,
        @NotBlank String frequency, @NotNull List<String> daysOfWeek, @NotNull List<@Valid ScheduleResource> schedules,
        String instructions, @NotBlank String status) {
    public record ScheduleResource(@NotBlank String time, @NotNull @DecimalMin("0") BigDecimal quantityPerAnimal) {
        FeedingPlan.Schedule toDomain() { return new FeedingPlan.Schedule(time, quantityPerAnimal); }
        static ScheduleResource from(FeedingPlan.Schedule schedule) { return new ScheduleResource(schedule.time(), schedule.quantityPerAnimal()); }
    }
    public FeedingPlan toDomain(String generatedId) { return FeedingPlan.create(id == null || id.isBlank() ? generatedId : id, lotId, animalId, name, startsOn, endsOn, objective, currentAverageWeight, targetWeight, frequency, daysOfWeek, schedules.stream().map(ScheduleResource::toDomain).toList(), instructions, status); }
    public static FeedingPlanResource from(FeedingPlan plan) { return new FeedingPlanResource(plan.getId(), plan.getLotId(), plan.getAnimalId(), plan.getName(), plan.getStartsOn(), plan.getEndsOn(), plan.getObjective(), plan.getCurrentAverageWeight(), plan.getTargetWeight(), plan.getFrequency(), plan.getDaysOfWeek(), plan.getSchedules().stream().map(ScheduleResource::from).toList(), plan.getInstructions(), plan.getStatus()); }
}

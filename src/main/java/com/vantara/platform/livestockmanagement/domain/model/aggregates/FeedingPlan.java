
package com.vantara.platform.livestockmanagement.domain.model.aggregates;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Defines a recurring feeding regimen for one lot or one animal.
 */
public final class FeedingPlan extends AbstractDomainAggregateRoot<FeedingPlan> {

    public record Schedule(String time, BigDecimal quantityPerAnimal) {
        public Schedule {
            if (time == null || time.isBlank()) {
                throw new IllegalArgumentException("Schedule time cannot be blank");
            }
            if (quantityPerAnimal == null || quantityPerAnimal.signum() < 0) {
                throw new IllegalArgumentException("Schedule quantity must be non-negative");
            }
            time = time.trim();
        }
    }

    private final String id;
    private final String lotId;
    private final String animalId;
    private final String name;
    private final LocalDate startsOn;
    private final LocalDate endsOn;
    private final String objective;
    private final BigDecimal currentAverageWeight;
    private final BigDecimal targetWeight;
    private final String frequency;
    private final List<String> daysOfWeek;
    private final List<Schedule> schedules;
    private final String instructions;
    private final String status;
    private final List<FeedingPlanItem> items;

    private FeedingPlan(
            String id,
            String lotId,
            String animalId,
            String name,
            LocalDate startsOn,
            LocalDate endsOn,
            String objective,
            BigDecimal currentAverageWeight,
            BigDecimal targetWeight,
            String frequency,
            List<String> daysOfWeek,
            List<Schedule> schedules,
            String instructions,
            String status,
            List<FeedingPlanItem> items
    ) {
        this.id = required(id, "Feeding plan id");
        this.lotId = required(lotId, "Lot id");
        this.animalId = blankToNull(animalId);
        this.name = required(name, "Plan name");

        this.startsOn = Objects.requireNonNull(startsOn, "Start date cannot be null");
        this.endsOn = Objects.requireNonNull(endsOn, "End date cannot be null");

        if (endsOn.isBefore(startsOn)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        this.objective = required(objective, "Objective");
        this.currentAverageWeight = nonNegative(currentAverageWeight, "Current average weight");
        this.targetWeight = nonNegative(targetWeight, "Target weight");
        this.frequency = required(frequency, "Frequency");

        this.daysOfWeek = List.copyOf(
                Objects.requireNonNull(daysOfWeek, "Days of week cannot be null")
        );

        this.schedules = List.copyOf(
                Objects.requireNonNull(schedules, "Schedules cannot be null")
        );

        this.instructions = instructions == null ? "" : instructions.trim();
        this.status = required(status, "Plan status");

        this.items = List.copyOf(
                Objects.requireNonNull(items, "Feeding plan items cannot be null")
        );

        for (FeedingPlanItem item : this.items) {
            if (!this.id.equals(item.getFeedingPlanId())) {
                throw new IllegalArgumentException(
                        "Feeding plan item must belong to this feeding plan"
                );
            }
        }
    }

    public static FeedingPlan create(
            String id,
            String lotId,
            String animalId,
            String name,
            LocalDate startsOn,
            LocalDate endsOn,
            String objective,
            BigDecimal currentAverageWeight,
            BigDecimal targetWeight,
            String frequency,
            List<String> daysOfWeek,
            List<Schedule> schedules,
            String instructions,
            String status,
            List<FeedingPlanItem> items
    ) {
        return new FeedingPlan(
                id, lotId, animalId, name, startsOn, endsOn,
                objective, currentAverageWeight, targetWeight,
                frequency, daysOfWeek, schedules, instructions,
                status, items
        );
    }

    private static String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static BigDecimal nonNegative(BigDecimal value, String label) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(label + " must be non-negative");
        }
        return value;
    }

    public String getId() { return id; }
    public String getLotId() { return lotId; }
    public String getAnimalId() { return animalId; }
    public String getName() { return name; }
    public LocalDate getStartsOn() { return startsOn; }
    public LocalDate getEndsOn() { return endsOn; }
    public String getObjective() { return objective; }
    public BigDecimal getCurrentAverageWeight() { return currentAverageWeight; }
    public BigDecimal getTargetWeight() { return targetWeight; }
    public String getFrequency() { return frequency; }
    public List<String> getDaysOfWeek() { return daysOfWeek; }
    public List<Schedule> getSchedules() { return schedules; }
    public String getInstructions() { return instructions; }
    public String getStatus() { return status; }
    public List<FeedingPlanItem> getItems() { return items; }
}

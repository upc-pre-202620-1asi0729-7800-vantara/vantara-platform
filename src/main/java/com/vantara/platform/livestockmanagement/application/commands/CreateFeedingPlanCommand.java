
package com.vantara.platform.livestockmanagement.application.commands;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request to create a feeding plan for a lot or animal.
 */
public record CreateFeedingPlanCommand(
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
        List<ScheduleData> schedules,
        String instructions,
        String status,
        List<ItemData> items
) {

    public record ScheduleData(
            String time,
            BigDecimal quantityPerAnimal
    ) {
        public ScheduleData {
            require(time, "schedule time");

            if (quantityPerAnimal == null || quantityPerAnimal.signum() < 0) {
                throw new IllegalArgumentException(
                        "Schedule quantity must be non-negative"
                );
            }

            time = time.trim();
        }
    }

    public record ItemData(
            String feedName,
            BigDecimal dailyQuantity,
            String unit
    ) {
        public ItemData {
            require(feedName, "feedName");
            require(unit, "unit");

            if (dailyQuantity == null || dailyQuantity.signum() < 0) {
                throw new IllegalArgumentException(
                        "Daily quantity must be non-negative"
                );
            }

            feedName = feedName.trim();
            unit = unit.trim();
        }
    }

    public CreateFeedingPlanCommand {
        require(lotId, "lotId");
        require(name, "name");
        require(objective, "objective");
        require(frequency, "frequency");
        require(status, "status");

        // Normalize optional animalId.
        animalId = animalId == null || animalId.isBlank()
                ? null
                : animalId.trim();

        if (startsOn == null || endsOn == null) {
            throw new IllegalArgumentException(
                    "Start and end dates cannot be null"
            );
        }

        if (endsOn.isBefore(startsOn)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        if (currentAverageWeight == null
                || currentAverageWeight.signum() < 0
                || targetWeight == null
                || targetWeight.signum() < 0) {
            throw new IllegalArgumentException(
                    "Weights must be non-negative"
            );
        }

        if (daysOfWeek == null || schedules == null || items == null) {
            throw new IllegalArgumentException(
                    "Days of week, schedules and items cannot be null"
            );
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "A feeding plan must contain at least one item"
            );
        }

        lotId = lotId.trim();
        name = name.trim();
        objective = objective.trim();
        frequency = frequency.trim();
        status = status.trim();

        daysOfWeek = List.copyOf(daysOfWeek);
        schedules = List.copyOf(schedules);
        items = List.copyOf(items);
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }
}

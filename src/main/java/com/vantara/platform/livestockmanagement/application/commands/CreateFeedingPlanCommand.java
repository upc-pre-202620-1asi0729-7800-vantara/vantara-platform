package com.vantara.platform.livestockmanagement.application.commands;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Request to create a feeding plan for a lot or animal. */
public record CreateFeedingPlanCommand(String lotId, String animalId, String name, LocalDate startsOn, LocalDate endsOn,
                                       String objective, BigDecimal currentAverageWeight, BigDecimal targetWeight,
                                       String frequency, List<String> daysOfWeek, List<FeedingPlan.Schedule> schedules,
                                       String instructions, String status) {
    public CreateFeedingPlanCommand {
        require(lotId, "lotId"); require(name, "name"); require(objective, "objective"); require(frequency, "frequency"); require(status, "status");
        if (startsOn == null || endsOn == null) throw new IllegalArgumentException("startsOn and endsOn cannot be null");
        if (endsOn.isBefore(startsOn)) throw new IllegalArgumentException("endsOn cannot be before startsOn");
        if (currentAverageWeight == null || currentAverageWeight.signum() < 0 || targetWeight == null || targetWeight.signum() < 0) throw new IllegalArgumentException("weights must be non-negative");
        if (daysOfWeek == null || schedules == null) throw new IllegalArgumentException("daysOfWeek and schedules cannot be null");
        daysOfWeek = List.copyOf(daysOfWeek); schedules = List.copyOf(schedules);
    }
    private static void require(String value, String name) { if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank"); }
}

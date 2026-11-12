package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.embeddables.FeedingScheduleEmbeddable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feeding_plans")
public class FeedingPlanJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 36) private String lotId;
    @Column(length = 36) private String animalId;
    @Column(nullable = false, length = 128) private String name;
    @Column(nullable = false) private LocalDate startsOn;
    @Column(nullable = false) private LocalDate endsOn;
    @Column(nullable = false, length = 512) private String objective;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal currentAverageWeight;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal targetWeight;
    @Column(nullable = false, length = 64) private String frequency;
    @ElementCollection @CollectionTable(name = "feeding_plan_days", joinColumns = @JoinColumn(name = "feeding_plan_id")) @Column(name = "day_of_week", nullable = false) private List<String> daysOfWeek = new ArrayList<>();
    @ElementCollection @CollectionTable(name = "feeding_plan_schedules", joinColumns = @JoinColumn(name = "feeding_plan_id")) private List<FeedingScheduleEmbeddable> schedules = new ArrayList<>();
    @Column(length = 4000) private String instructions;
    @Column(nullable = false, length = 32) private String status;
    protected FeedingPlanJpaEntity() { }
    private FeedingPlanJpaEntity(FeedingPlan plan) { id=plan.getId(); lotId=plan.getLotId(); animalId=plan.getAnimalId(); name=plan.getName(); startsOn=plan.getStartsOn(); endsOn=plan.getEndsOn(); objective=plan.getObjective(); currentAverageWeight=plan.getCurrentAverageWeight(); targetWeight=plan.getTargetWeight(); frequency=plan.getFrequency(); daysOfWeek=new ArrayList<>(plan.getDaysOfWeek()); schedules=plan.getSchedules().stream().map(FeedingScheduleEmbeddable::fromDomain).collect(java.util.stream.Collectors.toCollection(ArrayList::new)); instructions=plan.getInstructions(); status=plan.getStatus(); }
    public static FeedingPlanJpaEntity fromDomain(FeedingPlan plan) { return new FeedingPlanJpaEntity(plan); }
    public FeedingPlan toDomain() { return FeedingPlan.create(id, lotId, animalId, name, startsOn, endsOn, objective, currentAverageWeight, targetWeight, frequency, daysOfWeek, schedules.stream().map(FeedingScheduleEmbeddable::toDomain).toList(), instructions, status); }
}

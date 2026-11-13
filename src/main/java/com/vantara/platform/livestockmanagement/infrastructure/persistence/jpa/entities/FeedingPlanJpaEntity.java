
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.embeddables.FeedingScheduleEmbeddable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feeding_plans")
public class FeedingPlanJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String lotId;

    @Column(length = 36)
    private String animalId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false)
    private LocalDate startsOn;

    @Column(nullable = false)
    private LocalDate endsOn;

    @Column(nullable = false, length = 512)
    private String objective;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAverageWeight;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetWeight;

    @Column(nullable = false, length = 64)
    private String frequency;

    @ElementCollection
    @CollectionTable(
            name = "feeding_plan_days",
            joinColumns = @JoinColumn(name = "feeding_plan_id")
    )
    @Column(name = "day_of_week", nullable = false)
    private List<String> daysOfWeek = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "feeding_plan_schedules",
            joinColumns = @JoinColumn(name = "feeding_plan_id")
    )
    private List<FeedingScheduleEmbeddable> schedules = new ArrayList<>();

    @Column(length = 4000)
    private String instructions;

    @Column(nullable = false, length = 32)
    private String status;

    @OneToMany(
            mappedBy = "feedingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FeedingPlanItemJpaEntity> items = new ArrayList<>();

    protected FeedingPlanJpaEntity() {
    }

    public FeedingPlanJpaEntity(
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
            List<FeedingScheduleEmbeddable> schedules,
            String instructions,
            String status
    ) {
        this.id = id;
        this.lotId = lotId;
        this.animalId = animalId;
        this.name = name;
        this.startsOn = startsOn;
        this.endsOn = endsOn;
        this.objective = objective;
        this.currentAverageWeight = currentAverageWeight;
        this.targetWeight = targetWeight;
        this.frequency = frequency;
        this.daysOfWeek = new ArrayList<>(daysOfWeek);
        this.schedules = new ArrayList<>(schedules);
        this.instructions = instructions;
        this.status = status;
    }

    public void addItem(FeedingPlanItemJpaEntity item) {
        items.add(item);
        item.setFeedingPlan(this);
    }

    public String getId() {
        return id;
    }

    public String getLotId() {
        return lotId;
    }

    public String getAnimalId() {
        return animalId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public LocalDate getEndsOn() {
        return endsOn;
    }

    public String getObjective() {
        return objective;
    }

    public BigDecimal getCurrentAverageWeight() {
        return currentAverageWeight;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public String getFrequency() {
        return frequency;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public List<FeedingScheduleEmbeddable> getSchedules() {
        return schedules;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getStatus() {
        return status;
    }

    public List<FeedingPlanItemJpaEntity> getItems() {
        return items;
    }
}

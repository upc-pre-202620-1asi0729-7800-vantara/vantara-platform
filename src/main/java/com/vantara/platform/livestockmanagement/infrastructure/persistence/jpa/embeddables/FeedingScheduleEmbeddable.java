package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.embeddables;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class FeedingScheduleEmbeddable {
    private String time;
    private BigDecimal quantityPerAnimal;
    protected FeedingScheduleEmbeddable() { }
    public FeedingScheduleEmbeddable(String time, BigDecimal quantityPerAnimal) { this.time = time; this.quantityPerAnimal = quantityPerAnimal; }
    public static FeedingScheduleEmbeddable fromDomain(FeedingPlan.Schedule schedule) { return new FeedingScheduleEmbeddable(schedule.time(), schedule.quantityPerAnimal()); }
    public FeedingPlan.Schedule toDomain() { return new FeedingPlan.Schedule(time, quantityPerAnimal); }
}

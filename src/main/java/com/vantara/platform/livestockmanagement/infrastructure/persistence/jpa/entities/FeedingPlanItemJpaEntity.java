
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "feeding_plan_items")
public class FeedingPlanItemJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feeding_plan_id", nullable = false)
    private FeedingPlanJpaEntity feedingPlan;

    @Column(nullable = false, length = 128)
    private String feedName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dailyQuantity;

    @Column(nullable = false, length = 32)
    private String unit;

    protected FeedingPlanItemJpaEntity() {
    }

    public FeedingPlanItemJpaEntity(
            String id,
            String feedName,
            BigDecimal dailyQuantity,
            String unit
    ) {
        this.id = id;
        this.feedName = feedName;
        this.dailyQuantity = dailyQuantity;
        this.unit = unit;
    }

    public void setFeedingPlan(FeedingPlanJpaEntity feedingPlan) {
        this.feedingPlan = feedingPlan;
    }

    public String getId() {
        return id;
    }

    public FeedingPlanJpaEntity getFeedingPlan() {
        return feedingPlan;
    }

    public String getFeedName() {
        return feedName;
    }

    public BigDecimal getDailyQuantity() {
        return dailyQuantity;
    }

    public String getUnit() {
        return unit;
    }
}

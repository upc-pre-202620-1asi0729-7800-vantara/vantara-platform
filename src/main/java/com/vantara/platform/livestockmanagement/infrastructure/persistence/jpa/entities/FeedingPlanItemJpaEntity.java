package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "feeding_plan_items")
public class FeedingPlanItemJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 36) private String feedingPlanId;
    @Column(nullable = false, length = 128) private String feedName;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal dailyQuantity;
    @Column(nullable = false, length = 32) private String unit;
    protected FeedingPlanItemJpaEntity() { }
    private FeedingPlanItemJpaEntity(FeedingPlanItem item) { id=item.getId(); feedingPlanId=item.getFeedingPlanId(); feedName=item.getFeedName(); dailyQuantity=item.getDailyQuantity(); unit=item.getUnit(); }
    public static FeedingPlanItemJpaEntity fromDomain(FeedingPlanItem item) { return new FeedingPlanItemJpaEntity(item); }
    public FeedingPlanItem toDomain() { return FeedingPlanItem.create(id, feedingPlanId, feedName, dailyQuantity, unit); }
}

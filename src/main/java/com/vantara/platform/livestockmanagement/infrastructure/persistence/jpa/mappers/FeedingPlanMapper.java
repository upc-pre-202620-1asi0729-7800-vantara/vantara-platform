
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.embeddables.FeedingScheduleEmbeddable;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanItemJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingPlanJpaEntity;

import java.util.List;

public class FeedingPlanMapper {

    private FeedingPlanMapper() {
    }

    public static FeedingPlanJpaEntity toEntity(FeedingPlan plan) {

        FeedingPlanJpaEntity entity = new FeedingPlanJpaEntity(
                plan.getId(),
                plan.getLotId(),
                plan.getAnimalId(),
                plan.getName(),
                plan.getStartsOn(),
                plan.getEndsOn(),
                plan.getObjective(),
                plan.getCurrentAverageWeight(),
                plan.getTargetWeight(),
                plan.getFrequency(),
                plan.getDaysOfWeek(),
                plan.getSchedules().stream()
                        .map(FeedingScheduleEmbeddable::fromDomain)
                        .toList(),
                plan.getInstructions(),
                plan.getStatus()
        );

        for (FeedingPlanItem item : plan.getItems()) {
            FeedingPlanItemJpaEntity itemEntity =
                    new FeedingPlanItemJpaEntity(
                            item.getId(),
                            item.getFeedName(),
                            item.getDailyQuantity(),
                            item.getUnit()
                    );

            entity.addItem(itemEntity);
        }

        return entity;
    }

    public static FeedingPlan toDomain(FeedingPlanJpaEntity entity) {

        List<FeedingPlan.Schedule> schedules = entity.getSchedules()
                .stream()
                .map(FeedingScheduleEmbeddable::toDomain)
                .toList();

        List<FeedingPlanItem> items = entity.getItems()
                .stream()
                .map(item -> FeedingPlanItem.create(
                        item.getId(),
                        entity.getId(),
                        item.getFeedName(),
                        item.getDailyQuantity(),
                        item.getUnit()
                ))
                .toList();

        return FeedingPlan.create(
                entity.getId(),
                entity.getLotId(),
                entity.getAnimalId(),
                entity.getName(),
                entity.getStartsOn(),
                entity.getEndsOn(),
                entity.getObjective(),
                entity.getCurrentAverageWeight(),
                entity.getTargetWeight(),
                entity.getFrequency(),
                entity.getDaysOfWeek(),
                schedules,
                entity.getInstructions(),
                entity.getStatus(),
                items
        );
    }
}

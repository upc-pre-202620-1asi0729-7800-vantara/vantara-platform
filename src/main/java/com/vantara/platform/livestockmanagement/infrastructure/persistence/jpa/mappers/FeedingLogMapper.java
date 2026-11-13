
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingLogJpaEntity;

public class FeedingLogMapper {

    private FeedingLogMapper() {
    }

    public static FeedingLogJpaEntity toEntity(FeedingLog log) {
        return new FeedingLogJpaEntity(
                log.getId(),
                log.getPlanItemId(),
                log.getAnimalId(),
                log.getLotId(),
                log.getFeedName(),
                log.getFeedAt(),
                log.getQuantity(),
                log.getUnit(),
                log.getRecordedBy(),
                log.getNotes()
        );
    }

    public static FeedingLog toDomain(FeedingLogJpaEntity entity) {
        return FeedingLog.reconstitute(
                entity.getId(),
                entity.getPlanItemId(),
                entity.getAnimalId(),
                entity.getLotId(),
                entity.getFeedName(),
                entity.getFeedAt(),
                entity.getQuantity(),
                entity.getUnit(),
                entity.getRecordedBy(),
                entity.getNotes()
        );
    }
}

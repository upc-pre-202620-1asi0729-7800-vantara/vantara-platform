
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.LotJpaEntity;

public class LotMapper {

    private LotMapper() {
    }

    public static LotJpaEntity toEntity(Lot lot) {
        return new LotJpaEntity(
                lot.getId(),
                lot.getRancherId(),
                lot.getName(),
                lot.getPurpose(),
                lot.getStatus()
        );
    }

    public static Lot toDomain(LotJpaEntity entity) {
        return Lot.reconstitute(
                entity.getId(),
                entity.getRancherId(),
                entity.getName(),
                entity.getPurpose(),
                entity.getStatus()
        );
    }
}


package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.AnimalWeight;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.EarTag;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.AnimalJpaEntity;

public class AnimalMapper {

    private AnimalMapper() {
    }

    public static AnimalJpaEntity toEntity(Animal animal) {
        return new AnimalJpaEntity(
                animal.getId(),
                animal.getRancherId(),
                animal.getLotId(),
                animal.getEarTag().value(),
                animal.getName(),
                animal.getSpecies(),
                animal.getBreed(),
                animal.getSex(),
                animal.getBirthDate(),
                animal.getMotherId(),
                animal.getFatherId(),
                animal.getWeight().value(),
                animal.getStatus(),
                animal.getRegisteredAt(),
                animal.getPhotoUrl(),
                animal.getWeanedOn()
        );
    }

    public static Animal toDomain(AnimalJpaEntity entity) {
        return Animal.reconstitute(
                entity.getId(),
                entity.getRancherId(),
                entity.getLotId(),
                new EarTag(entity.getEarTag()),
                entity.getName(),
                entity.getSpecies(),
                entity.getBreed(),
                entity.getSex(),
                entity.getBirthDate(),
                entity.getMotherId(),
                entity.getFatherId(),
                new AnimalWeight(entity.getWeight()),
                entity.getStatus(),
                entity.getRegisteredAt(),
                entity.getPhotoUrl(),
                entity.getWeanedOn()
        );
    }
}

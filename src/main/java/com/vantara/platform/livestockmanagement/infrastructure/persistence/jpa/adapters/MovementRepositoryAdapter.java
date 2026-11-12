
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.repositories.MovementRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.MovementJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.MovementMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.MovementJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovementRepositoryAdapter implements MovementRepository {

    private final MovementJpaRepository repository;

    public MovementRepositoryAdapter(MovementJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Movement save(Movement movement) {
        MovementJpaEntity entity = MovementMapper.toEntity(movement);
        MovementJpaEntity savedEntity = repository.save(entity);

        return MovementMapper.toDomain(savedEntity);
    }

    @Override
    public List<Movement> findAll() {
        return repository.findAll()
                .stream()
                .map(MovementMapper::toDomain)
                .toList();
    }

    @Override
    public List<Movement> findAllByAnimalId(String animalId) {
        return repository.findAllByAnimalId(animalId)
                .stream()
                .map(MovementMapper::toDomain)
                .toList();
    }

    @Override
    public List<Movement> findAllByOriginLotIdOrDestinationLotId(String lotId) {
        return repository.findAllByOriginLotIdOrDestinationLotId(lotId, lotId)
                .stream()
                .map(MovementMapper::toDomain)
                .toList();
    }
}

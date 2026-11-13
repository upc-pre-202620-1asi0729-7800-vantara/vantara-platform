
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.repositories.MovementRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.MovementJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.MovementMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.MovementJpaRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovementRepositoryAdapter implements MovementRepository {

    private final MovementJpaRepository repository;
    private final DomainEventPublisher eventPublisher;

    public MovementRepositoryAdapter(
            MovementJpaRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Movement save(Movement movement) {
        MovementJpaEntity entity = MovementMapper.toEntity(movement);
        MovementJpaEntity savedEntity = repository.save(entity);
        var saved = MovementMapper.toDomain(savedEntity);

        eventPublisher.publishAndClear(movement);

        return saved;
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

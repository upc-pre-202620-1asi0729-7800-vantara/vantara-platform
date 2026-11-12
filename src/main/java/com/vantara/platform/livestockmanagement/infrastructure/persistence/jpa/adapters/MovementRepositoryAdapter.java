package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.repositories.MovementRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.MovementJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.MovementJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MovementRepositoryAdapter implements MovementRepository {
    private final MovementJpaRepository repository;
    public MovementRepositoryAdapter(MovementJpaRepository repository) { this.repository = repository; }
    public Movement save(Movement movement) { return repository.save(MovementJpaEntity.fromDomain(movement)).toDomain(); }
    public List<Movement> findAll() { return repository.findAll().stream().map(MovementJpaEntity::toDomain).toList(); }
    public List<Movement> findAllByAnimalId(String animalId) { return repository.findAllByAnimalId(animalId).stream().map(MovementJpaEntity::toDomain).toList(); }
}


package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.repositories.AnimalRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.AnimalJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.AnimalMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.AnimalJpaRepository;

import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AnimalRepositoryAdapter implements AnimalRepository {

    private final AnimalJpaRepository repository;
    private final DomainEventPublisher eventPublisher;

    public AnimalRepositoryAdapter(
            AnimalJpaRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Animal save(Animal animal) {
        AnimalJpaEntity entity = AnimalMapper.toEntity(animal);
        var saved = AnimalMapper.toDomain(repository.save(entity));

        // Events recorded on the original aggregate are published once the state is stored.
        eventPublisher.publishAndClear(animal);

        return saved;
    }

    @Override
    public Optional<Animal> findById(String id) {
        return repository.findById(id)
                .map(AnimalMapper::toDomain);
    }

    @Override
    public Optional<Animal> findByIdAndRancherId(
            String id,
            String rancherId
    ) {
        return repository.findByIdAndRancherId(id, rancherId)
                .map(AnimalMapper::toDomain);
    }

    @Override
    public List<Animal> findAll() {
        return repository.findAll()
                .stream()
                .map(AnimalMapper::toDomain)
                .toList();
    }

    @Override
    public List<Animal> findAllByRancherId(String rancherId) {
        return repository.findAllByRancherId(rancherId)
                .stream()
                .map(AnimalMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEarTagAndRancherId(
            String earTag,
            String rancherId
    ) {
        return repository.existsByEarTagAndRancherId(
                earTag,
                rancherId
        );
    }

    @Override
    public void delete(Animal animal) {
        repository.deleteById(animal.getId());

        // The removal event is published only after the row is gone.
        eventPublisher.publishAndClear(animal);
    }
}

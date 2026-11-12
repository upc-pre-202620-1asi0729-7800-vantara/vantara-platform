
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.repositories.AnimalRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.AnimalJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.AnimalMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.AnimalJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AnimalRepositoryAdapter implements AnimalRepository {

    private final AnimalJpaRepository repository;

    public AnimalRepositoryAdapter(AnimalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Animal save(Animal animal) {
        AnimalJpaEntity entity = AnimalMapper.toEntity(animal);
        return AnimalMapper.toDomain(repository.save(entity));
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
        return repository.findById(id)
                .filter(animal -> animal.getRancherId().equals(rancherId))
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
        return repository.findAll()
                .stream()
                .filter(animal -> animal.getRancherId().equals(rancherId))
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
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

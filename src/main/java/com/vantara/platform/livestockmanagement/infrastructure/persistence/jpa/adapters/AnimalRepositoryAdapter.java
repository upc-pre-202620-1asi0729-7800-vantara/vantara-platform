package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.repositories.AnimalRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.AnimalJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.AnimalJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class AnimalRepositoryAdapter implements AnimalRepository {
    private final AnimalJpaRepository repository;
    public AnimalRepositoryAdapter(AnimalJpaRepository repository) { this.repository = repository; }
    public Animal save(Animal animal) { return repository.save(AnimalJpaEntity.fromDomain(animal)).toDomain(); }
    public Optional<Animal> findById(String id) { return repository.findById(id).map(AnimalJpaEntity::toDomain); }
    public List<Animal> findAll() { return repository.findAll().stream().map(AnimalJpaEntity::toDomain).toList(); }
    public boolean existsByEarTag(String earTag) { return repository.existsByEarTag(earTag); }
    public void deleteById(String id) { repository.deleteById(id); }
}

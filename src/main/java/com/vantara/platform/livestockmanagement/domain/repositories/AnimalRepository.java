package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import java.util.List;
import java.util.Optional;

/** Persistence port for livestock animals. */
public interface AnimalRepository {
    Animal save(Animal animal);
    Optional<Animal> findById(String id);
    List<Animal> findAll();
    boolean existsByEarTag(String earTag);
    void deleteById(String id);
}

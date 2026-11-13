
package com.vantara.platform.livestockmanagement.domain.repositories;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for livestock animals.
 */
public interface AnimalRepository {

    Animal save(Animal animal);

    Optional<Animal> findById(String id);

    Optional<Animal> findByIdAndRancherId(String id, String rancherId);

    List<Animal> findAll();

    List<Animal> findAllByRancherId(String rancherId);

    boolean existsByEarTagAndRancherId(String earTag, String rancherId);

    /**
     * Deletes the animal and publishes the events it recorded (for example its removal).
     */
    void delete(Animal animal);
}

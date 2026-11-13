
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.AnimalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalJpaRepository extends JpaRepository<AnimalJpaEntity, String> {

    boolean existsByEarTagAndRancherId(String earTag, String rancherId);

    Optional<AnimalJpaEntity> findByIdAndRancherId(String id, String rancherId);

    List<AnimalJpaEntity> findAllByRancherId(String rancherId);
}


package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.LotJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LotJpaRepository
        extends JpaRepository<LotJpaEntity, String> {

    Optional<LotJpaEntity> findByIdAndRancherId(
            String id,
            String rancherId
    );

    List<LotJpaEntity> findAllByRancherId(String rancherId);
}

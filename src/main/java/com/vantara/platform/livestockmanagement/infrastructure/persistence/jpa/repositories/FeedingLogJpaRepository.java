package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedingLogJpaRepository extends JpaRepository<FeedingLogJpaEntity, String> {
    List<FeedingLogJpaEntity> findAllByLotId(String lotId);
    List<FeedingLogJpaEntity> findAllByAnimalId(String animalId);
}

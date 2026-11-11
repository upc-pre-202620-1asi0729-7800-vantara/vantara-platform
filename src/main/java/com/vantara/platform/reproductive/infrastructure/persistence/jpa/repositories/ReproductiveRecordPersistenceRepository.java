package com.vantara.platform.reproductive.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.reproductive.domain.model.valueobjects.ReproductiveStatus;
import com.vantara.platform.reproductive.infrastructure.persistence.jpa.entities.ReproductiveRecordPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data repository for reproductive record persistence entities.
 */
@Repository
public interface ReproductiveRecordPersistenceRepository extends JpaRepository<ReproductiveRecordPersistenceEntity, Long> {

    List<ReproductiveRecordPersistenceEntity> findAllByOrderByConfirmedOnAscIdAsc();

    List<ReproductiveRecordPersistenceEntity> findAllByAnimalIdOrderByConfirmedOnAscIdAsc(Long animalId);

    boolean existsByAnimalIdAndStatusIn(Long animalId, Collection<ReproductiveStatus> statuses);
}

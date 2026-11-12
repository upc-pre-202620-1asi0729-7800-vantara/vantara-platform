package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingLog;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingLogRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingLogJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingLogJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FeedingLogRepositoryAdapter implements FeedingLogRepository {
    private final FeedingLogJpaRepository repository;
    public FeedingLogRepositoryAdapter(FeedingLogJpaRepository repository) { this.repository = repository; }
    public FeedingLog save(FeedingLog log) { return repository.save(FeedingLogJpaEntity.fromDomain(log)).toDomain(); }
    public List<FeedingLog> findAll() { return repository.findAll().stream().map(FeedingLogJpaEntity::toDomain).toList(); }
    public List<FeedingLog> findAllByLotId(String lotId) { return repository.findAllByLotId(lotId).stream().map(FeedingLogJpaEntity::toDomain).toList(); }
    public List<FeedingLog> findAllByAnimalId(String animalId) { return repository.findAllByAnimalId(animalId).stream().map(FeedingLogJpaEntity::toDomain).toList(); }
}

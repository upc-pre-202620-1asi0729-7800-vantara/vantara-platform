
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingLogRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.FeedingLogJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.FeedingLogMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.FeedingLogJpaRepository;

import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FeedingLogRepositoryAdapter implements FeedingLogRepository {

    private final FeedingLogJpaRepository repository;
    private final DomainEventPublisher eventPublisher;

    public FeedingLogRepositoryAdapter(
            FeedingLogJpaRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public FeedingLog save(FeedingLog log) {
        FeedingLogJpaEntity entity = FeedingLogMapper.toEntity(log);

        var saved = FeedingLogMapper.toDomain(repository.save(entity));

        eventPublisher.publishAndClear(log);

        return saved;
    }

    @Override
    public List<FeedingLog> findAll() {
        return repository.findAll()
                .stream()
                .map(FeedingLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<FeedingLog> findAllByLotId(String lotId) {
        return repository.findAllByLotId(lotId)
                .stream()
                .map(FeedingLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<FeedingLog> findAllByAnimalId(String animalId) {
        return repository.findAllByAnimalId(animalId)
                .stream()
                .map(FeedingLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<FeedingLog> findAllByLotIdAndAnimalId(
            String lotId,
            String animalId
    ) {
        return repository.findAllByLotIdAndAnimalId(lotId, animalId)
                .stream()
                .map(FeedingLogMapper::toDomain)
                .toList();
    }
}


package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.repositories.LotRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.LotJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.mappers.LotMapper;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.LotJpaRepository;

import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LotRepositoryAdapter implements LotRepository {

    private final LotJpaRepository repository;
    private final DomainEventPublisher eventPublisher;

    public LotRepositoryAdapter(
            LotJpaRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Lot save(Lot lot) {
        LotJpaEntity entity = LotMapper.toEntity(lot);
        var saved = LotMapper.toDomain(repository.save(entity));

        eventPublisher.publishAndClear(lot);

        return saved;
    }

    @Override
    public Optional<Lot> findById(String id) {
        return repository.findById(id)
                .map(LotMapper::toDomain);
    }

    @Override
    public Optional<Lot> findByIdAndRancherId(
            String id,
            String rancherId
    ) {
        return repository.findByIdAndRancherId(id, rancherId)
                .map(LotMapper::toDomain);
    }

    @Override
    public List<Lot> findAll() {
        return repository.findAll()
                .stream()
                .map(LotMapper::toDomain)
                .toList();
    }

    @Override
    public List<Lot> findAllByRancherId(String rancherId) {
        return repository.findAllByRancherId(rancherId)
                .stream()
                .map(LotMapper::toDomain)
                .toList();
    }
}

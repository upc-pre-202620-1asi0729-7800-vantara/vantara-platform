package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.repositories.LotRepository;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities.LotJpaEntity;
import com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.repositories.LotJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class LotRepositoryAdapter implements LotRepository {
    private final LotJpaRepository repository;
    public LotRepositoryAdapter(LotJpaRepository repository) { this.repository = repository; }
    public Lot save(Lot lot) { return repository.save(LotJpaEntity.fromDomain(lot)).toDomain(); }
    public Optional<Lot> findById(String id) { return repository.findById(id).map(LotJpaEntity::toDomain); }
    public List<Lot> findAll() { return repository.findAll().stream().map(LotJpaEntity::toDomain).toList(); }
}

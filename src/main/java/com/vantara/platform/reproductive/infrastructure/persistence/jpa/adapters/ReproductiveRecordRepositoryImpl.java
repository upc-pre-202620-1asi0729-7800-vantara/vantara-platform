package com.vantara.platform.reproductive.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.model.valueobjects.ReproductiveStatus;
import com.vantara.platform.reproductive.domain.repositories.ReproductiveRecordRepository;
import com.vantara.platform.reproductive.infrastructure.persistence.jpa.assemblers.ReproductiveRecordPersistenceAssembler;
import com.vantara.platform.reproductive.infrastructure.persistence.jpa.repositories.ReproductiveRecordPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the reproductive record domain port with Spring Data JPA.
 *
 * <p>It is also the event-publishing boundary: after saving, the domain events recorded by the aggregate are published
 * and cleared. For a brand-new record the "pregnancy registered" event is recorded once the identifier exists.</p>
 */
@Repository
public class ReproductiveRecordRepositoryImpl implements ReproductiveRecordRepository {

    private final ReproductiveRecordPersistenceRepository persistenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public ReproductiveRecordRepositoryImpl(
            ReproductiveRecordPersistenceRepository persistenceRepository,
            ApplicationEventPublisher eventPublisher,
            Clock clock) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    public ReproductiveRecord save(ReproductiveRecord record) {
        boolean isNew = record.getId() == null;
        var savedEntity = persistenceRepository.save(ReproductiveRecordPersistenceAssembler.toPersistenceFromDomain(record));
        var saved = ReproductiveRecordPersistenceAssembler.toDomainFromPersistence(savedEntity);

        var events = new ArrayList<>(record.domainEvents());
        record.clearDomainEvents();
        if (isNew) {
            saved.onRegistered(clock.instant());
            events.addAll(saved.domainEvents());
            saved.clearDomainEvents();
        }
        events.forEach(eventPublisher::publishEvent);
        return saved;
    }

    @Override
    public Optional<ReproductiveRecord> findById(Long id) {
        return persistenceRepository.findById(id).map(ReproductiveRecordPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<ReproductiveRecord> findAll() {
        return persistenceRepository.findAllByOrderByConfirmedOnAscIdAsc().stream()
                .map(ReproductiveRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<ReproductiveRecord> findAllByAnimalId(AnimalId animalId) {
        return persistenceRepository.findAllByAnimalIdOrderByConfirmedOnAscIdAsc(animalId.animalId()).stream()
                .map(ReproductiveRecordPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsPregnancyInProgressByAnimalId(AnimalId animalId) {
        return persistenceRepository.existsByAnimalIdAndStatusIn(
                animalId.animalId(), EnumSet.of(ReproductiveStatus.CONFIRMED, ReproductiveStatus.DRY_OFF));
    }
}

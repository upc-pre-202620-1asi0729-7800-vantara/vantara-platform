package com.vantara.platform.iam.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.iam.domain.model.aggregates.VerificationCode;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.iam.domain.repositories.VerificationCodeRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers.VerificationCodePersistenceAssembler;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.VerificationCodePersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.repositories.VerificationCodePersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements the {@link VerificationCodeRepository} domain port with Spring Data JPA.
 */
@Repository
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {

    private final VerificationCodePersistenceRepository persistenceRepository;
    private final DomainEventPublisher eventPublisher;

    public VerificationCodeRepositoryImpl(
            VerificationCodePersistenceRepository persistenceRepository, DomainEventPublisher eventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        var entity = verificationCode.getId() == null
                ? new VerificationCodePersistenceEntity()
                : persistenceRepository.findById(verificationCode.getId())
                        .orElseGet(VerificationCodePersistenceEntity::new);
        VerificationCodePersistenceAssembler.toPersistenceFromDomain(entity, verificationCode);
        var saved = VerificationCodePersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
        eventPublisher.publishAndClear(verificationCode);
        return saved;
    }

    @Override
    public Optional<VerificationCode> findLatestByUserIdAndPurpose(UserId userId, VerificationPurpose purpose) {
        return persistenceRepository.findFirstByUserIdAndPurposeOrderByIssuedAtDescIdDesc(userId, purpose)
                .map(VerificationCodePersistenceAssembler::toDomainFromPersistence);
    }
}

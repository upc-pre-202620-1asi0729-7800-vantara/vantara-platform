package com.vantara.platform.iam.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.repositories.ContactRequestRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers.ContactRequestPersistenceAssembler;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.ContactRequestPersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.repositories.ContactRequestPersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

/**
 * Adapter that implements the {@link ContactRequestRepository} domain port with Spring Data JPA.
 */
@Repository
public class ContactRequestRepositoryImpl implements ContactRequestRepository {

    private final ContactRequestPersistenceRepository persistenceRepository;
    private final DomainEventPublisher eventPublisher;

    public ContactRequestRepositoryImpl(
            ContactRequestPersistenceRepository persistenceRepository, DomainEventPublisher eventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ContactRequest save(ContactRequest contactRequest) {
        var entity = contactRequest.getId() == null
                ? new ContactRequestPersistenceEntity()
                : persistenceRepository.findById(contactRequest.getId())
                        .orElseGet(ContactRequestPersistenceEntity::new);
        ContactRequestPersistenceAssembler.toPersistenceFromDomain(entity, contactRequest);
        var saved = ContactRequestPersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
        eventPublisher.publishAndClear(contactRequest);
        return saved;
    }
}

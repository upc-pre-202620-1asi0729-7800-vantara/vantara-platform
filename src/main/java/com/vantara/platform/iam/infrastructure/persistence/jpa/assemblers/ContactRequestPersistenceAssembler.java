package com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.model.valueobjects.ContactMessage;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.ContactRequestPersistenceEntity;

/**
 * Translates between the {@link ContactRequest} aggregate and its persistence entity.
 */
public final class ContactRequestPersistenceAssembler {

    private ContactRequestPersistenceAssembler() {
    }

    public static ContactRequest toDomainFromPersistence(ContactRequestPersistenceEntity entity) {
        return ContactRequest.reconstitute(
                entity.getId(),
                new FullName(entity.getFullName()),
                entity.getEmail(),
                new ContactMessage(entity.getMessage()),
                entity.getSentAt());
    }

    public static ContactRequestPersistenceEntity toPersistenceFromDomain(
            ContactRequestPersistenceEntity target, ContactRequest request) {
        target.setFullName(request.getFullName().value());
        target.setEmail(request.getEmail());
        target.setMessage(request.getMessage().content());
        target.setSentAt(request.getSentAt());
        return target;
    }
}

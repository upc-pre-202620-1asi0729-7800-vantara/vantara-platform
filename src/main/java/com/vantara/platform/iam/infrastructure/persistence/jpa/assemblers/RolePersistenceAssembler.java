package com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;

/**
 * Translates between the {@link Role} domain entity and its persistence entity.
 */
public final class RolePersistenceAssembler {

    private RolePersistenceAssembler() {
    }

    public static Role toDomainFromPersistence(RolePersistenceEntity entity) {
        return Role.reconstitute(entity.getId(), entity.getName());
    }

    public static RolePersistenceEntity toPersistenceFromDomain(Role role) {
        var entity = new RolePersistenceEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        return entity;
    }
}

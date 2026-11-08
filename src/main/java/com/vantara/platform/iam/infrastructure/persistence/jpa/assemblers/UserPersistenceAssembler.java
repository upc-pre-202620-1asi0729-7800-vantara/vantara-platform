package com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.Collection;

/**
 * Translates between the {@link User} aggregate and its persistence entity.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    /** Rebuilds the aggregate through {@code reconstitute}, so no domain event is recorded. */
    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        var roles = entity.getRoles().stream().map(RolePersistenceAssembler::toDomainFromPersistence).toList();
        return User.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                roles,
                entity.getStatus(),
                entity.getLastSignInAt());
    }

    /**
     * Copies the state of the aggregate into the given persistence entity.
     *
     * @param target the entity to fill: a new one, or the stored one when the user already exists
     * @param user   the aggregate to copy
     * @param roles  the stored role entities that match the roles of the user
     * @return the same {@code target}
     */
    public static UserPersistenceEntity toPersistenceFromDomain(
            UserPersistenceEntity target, User user, Collection<RolePersistenceEntity> roles) {
        target.setEmail(user.getEmail());
        target.setPasswordHash(user.getPasswordHash());
        target.setStatus(user.getStatus());
        target.setLastSignInAt(user.getLastSignInAt());
        target.getRoles().clear();
        target.getRoles().addAll(roles);
        return target;
    }
}

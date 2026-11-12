package com.vantara.platform.profile.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.profile.domain.model.aggregates.Profile;
import com.vantara.platform.profile.domain.model.valueobjects.ProfilePreferences;
import com.vantara.platform.profile.infrastructure.persistence.jpa.entities.ProfileJpaEntity;

import java.time.Instant;
import java.util.Date;

/**
 * Translates between the {@link Profile} aggregate and its persistence entity.
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    /** Rebuilds the aggregate through {@code reconstitute}, so no domain event is recorded. */
    public static Profile toDomainFromPersistence(ProfileJpaEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getFullName(),
                entity.getOrganizationName(),
                entity.getPhone(),
                entity.getPhotoUrl(),
                entity.getTheme(),
                entity.getLocale(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /**
     * Copies the state of the aggregate into the given persistence entity.
     *
     * @param target  the entity to fill: a new one, or the stored one when the profile already exists
     * @param profile the aggregate to copy
     * @return the same {@code target}
     */
    public static ProfileJpaEntity toPersistenceFromDomain(ProfileJpaEntity target, Profile profile) {
        target.setUserId(profile.getUserId());
        target.setFullName(profile.getFullName());
        target.setOrganizationName(profile.getOrganizationName());
        target.setPhone(profile.getPhone());
        target.setPhotoUrl(profile.getPhotoUrl());
        target.setTheme(profile.getPreferences().theme());
        target.setLocale(profile.getPreferences().locale());
        return target;
    }

    private static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }
}

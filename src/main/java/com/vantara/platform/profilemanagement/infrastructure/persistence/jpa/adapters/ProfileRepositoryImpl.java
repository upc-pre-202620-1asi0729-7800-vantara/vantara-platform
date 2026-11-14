package com.vantara.platform.profilemanagement.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.domain.repositories.ProfileRepository;
import com.vantara.platform.profilemanagement.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.vantara.platform.profilemanagement.infrastructure.persistence.jpa.entities.ProfileJpaEntity;
import com.vantara.platform.profilemanagement.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA adapter for the profile repository port.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository persistenceRepository;

    public ProfileRepositoryImpl(ProfilePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Profile save(Profile profile) {
        var entity = profile.getId() == null
                ? new ProfileJpaEntity()
                : persistenceRepository.findById(profile.getId()).orElseGet(ProfileJpaEntity::new);
        ProfilePersistenceAssembler.toPersistenceFromDomain(entity, profile);
        return ProfilePersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
    }

    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return persistenceRepository.findByUserId(userId).map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }
}

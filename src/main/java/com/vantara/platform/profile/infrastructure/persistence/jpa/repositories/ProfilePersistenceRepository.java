package com.vantara.platform.profile.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.profile.infrastructure.persistence.jpa.entities.ProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository of profile persistence entities.
 */
public interface ProfilePersistenceRepository extends JpaRepository<ProfileJpaEntity, Long> {

    Optional<ProfileJpaEntity> findByUserId(Long userId);
}

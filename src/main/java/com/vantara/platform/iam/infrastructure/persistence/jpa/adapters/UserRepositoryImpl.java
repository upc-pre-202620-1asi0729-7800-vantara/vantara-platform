package com.vantara.platform.iam.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link UserRepository} domain port with Spring Data JPA.
 *
 * <p>After saving, it publishes the domain events recorded by the aggregate.</p>
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository persistenceRepository;
    private final RolePersistenceRepository rolePersistenceRepository;
    private final DomainEventPublisher eventPublisher;

    public UserRepositoryImpl(
            UserPersistenceRepository persistenceRepository,
            RolePersistenceRepository rolePersistenceRepository,
            DomainEventPublisher eventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.rolePersistenceRepository = rolePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * @throws IllegalStateException when a role of the user was never stored (roles are seeded at startup)
     */
    @Override
    public User save(User user) {
        var entity = user.getId() == null
                ? new UserPersistenceEntity()
                : persistenceRepository.findById(user.getId()).orElseGet(UserPersistenceEntity::new);
        List<RolePersistenceEntity> roles = user.getRoles().stream()
                .map(role -> rolePersistenceRepository.findByName(role.getName())
                        .orElseThrow(() -> new IllegalStateException("Role is not stored: " + role.getName())))
                .toList();
        UserPersistenceAssembler.toPersistenceFromDomain(entity, user, roles);
        var saved = UserPersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
        // Events recorded on the original aggregate are published once the state is stored.
        eventPublisher.publishAndClear(user);
        return saved;
    }

    @Override
    public Optional<User> findById(Long id) {
        return persistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return persistenceRepository.findByEmail(email).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return persistenceRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return persistenceRepository.findAll().stream()
                .map(UserPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

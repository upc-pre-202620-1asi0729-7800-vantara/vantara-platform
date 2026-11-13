package com.vantara.platform.iam.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers.RolePersistenceAssembler;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.vantara.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link RoleRepository} domain port with Spring Data JPA.
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RolePersistenceRepository persistenceRepository;

    public RoleRepositoryImpl(RolePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Role save(Role role) {
        var entity = role.getId() == null
                ? new RolePersistenceEntity()
                : persistenceRepository.findById(role.getId()).orElseGet(RolePersistenceEntity::new);
        entity.setName(role.getName());
        return RolePersistenceAssembler.toDomainFromPersistence(persistenceRepository.save(entity));
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return persistenceRepository.findByName(name).map(RolePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByName(Roles name) {
        return persistenceRepository.existsByName(name);
    }

    @Override
    public List<Role> findAll() {
        return persistenceRepository.findAll().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

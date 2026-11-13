package com.vantara.platform.iam.application.internal.queryservices;

import com.vantara.platform.iam.application.queryservices.RoleQueryService;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.vantara.platform.iam.domain.model.queries.GetRoleByNameQuery;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> handle(GetAllRolesQuery query) {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> handle(GetRoleByNameQuery query) {
        return roleRepository.findByName(query.name());
    }
}

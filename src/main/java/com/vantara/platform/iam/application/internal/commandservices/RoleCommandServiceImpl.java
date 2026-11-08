package com.vantara.platform.iam.application.internal.commandservices;

import com.vantara.platform.iam.application.commandservices.RoleCommandService;
import com.vantara.platform.iam.domain.model.commands.SeedRolesCommand;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values())
                .filter(name -> !roleRepository.existsByName(name))
                .forEach(name -> roleRepository.save(new Role(name)));
    }
}

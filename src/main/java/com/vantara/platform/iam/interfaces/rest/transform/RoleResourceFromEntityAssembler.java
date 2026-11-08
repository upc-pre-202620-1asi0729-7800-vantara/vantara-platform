package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.interfaces.rest.resources.RoleResource;

public final class RoleResourceFromEntityAssembler {

    private RoleResourceFromEntityAssembler() {
    }

    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}

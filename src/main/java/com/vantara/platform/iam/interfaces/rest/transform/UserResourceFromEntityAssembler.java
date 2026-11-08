package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.interfaces.rest.resources.UserResource;

public final class UserResourceFromEntityAssembler {

    private UserResourceFromEntityAssembler() {
    }

    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream().map(Role::getStringName).sorted().toList();
        return new UserResource(
                user.getId(), user.getEmail().address(), user.getStatus().name(), roles, user.getLastSignInAt());
    }
}

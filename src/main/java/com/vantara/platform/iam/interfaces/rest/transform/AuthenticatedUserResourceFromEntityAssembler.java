package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.application.commandservices.AuthenticatedUser;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public final class AuthenticatedUserResourceFromEntityAssembler {

    private AuthenticatedUserResourceFromEntityAssembler() {
    }

    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedUser authenticated) {
        var user = authenticated.user();
        var roles = user.getRoles().stream().map(Role::getStringName).sorted().toList();
        return new AuthenticatedUserResource(user.getId(), user.getEmail().address(), roles, authenticated.token());
    }
}

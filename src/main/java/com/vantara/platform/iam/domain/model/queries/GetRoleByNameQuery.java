package com.vantara.platform.iam.domain.model.queries;

import com.vantara.platform.iam.domain.model.valueobjects.Roles;

/**
 * Query to retrieve a role by name.
 *
 * @param name the role name
 */
public record GetRoleByNameQuery(Roles name) {
    public GetRoleByNameQuery {
        if (name == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
    }
}

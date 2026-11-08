package com.vantara.platform.iam.application.queryservices;

import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.vantara.platform.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

/**
 * Read-side contract of the role queries.
 */
public interface RoleQueryService {

    List<Role> handle(GetAllRolesQuery query);

    Optional<Role> handle(GetRoleByNameQuery query);
}

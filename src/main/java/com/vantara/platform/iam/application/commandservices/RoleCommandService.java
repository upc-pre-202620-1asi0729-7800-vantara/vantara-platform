package com.vantara.platform.iam.application.commandservices;

import com.vantara.platform.iam.domain.model.commands.SeedRolesCommand;

/**
 * Write-side contract of the role use cases.
 */
public interface RoleCommandService {

    /**
     * Creates the roles that do not exist yet. It is idempotent.
     */
    void handle(SeedRolesCommand command);
}

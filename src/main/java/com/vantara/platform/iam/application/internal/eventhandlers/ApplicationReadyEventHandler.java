package com.vantara.platform.iam.application.internal.eventhandlers;

import com.vantara.platform.iam.application.commandservices.RoleCommandService;
import com.vantara.platform.iam.domain.model.commands.SeedRolesCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Seeds the platform roles when the application is ready. The seed is idempotent.
 */
@Component
public class ApplicationReadyEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);

    private final RoleCommandService roleCommandService;

    public ApplicationReadyEventHandler(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        roleCommandService.handle(new SeedRolesCommand());
        LOGGER.info("IAM roles are seeded");
    }
}

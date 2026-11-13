package com.vantara.platform.notifications.interfaces.rest.transform;

import com.vantara.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.vantara.platform.notifications.interfaces.rest.resources.CreateNotificationResource;

public final class CreateNotificationCommandFromResourceAssembler {
    private CreateNotificationCommandFromResourceAssembler() {
    }

    public static CreateNotificationCommand toCommandFromResource(Long recipientId,
                                                                  CreateNotificationResource resource) {
        return new CreateNotificationCommand(recipientId, resource.type(), resource.title(),
                resource.description(), resource.relatedEntityType(), resource.relatedEntityId());
    }
}

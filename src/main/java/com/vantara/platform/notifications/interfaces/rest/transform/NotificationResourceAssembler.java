package com.vantara.platform.notifications.interfaces.rest.transform;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.interfaces.rest.resources.NotificationResource;

import java.util.Locale;

public final class NotificationResourceAssembler {
    private NotificationResourceAssembler() {
    }

    public static NotificationResource fromDomain(Notification notification) {
        return new NotificationResource(notification.getId().value().toString(),
                Long.toString(notification.getRecipient().value()), notification.getType(),
                notification.getContent().title(), notification.getContent().description(), "in_app",
                notification.getStatus().name().toLowerCase(Locale.ROOT),
                notification.getScheduledAt(), notification.getSentAt(), notification.getReadAt(),
                notification.getRelatedEntity().type(), notification.getRelatedEntity().id(),
                notification.getCreatedAt());
    }
}

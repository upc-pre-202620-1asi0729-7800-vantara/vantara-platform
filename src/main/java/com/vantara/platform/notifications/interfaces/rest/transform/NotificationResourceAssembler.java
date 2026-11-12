package com.vantara.platform.notifications.interfaces.rest.transform;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.interfaces.rest.resources.NotificationResource;

import java.util.Locale;

public final class NotificationResourceAssembler {
    private NotificationResourceAssembler() {
    }

    public static NotificationResource fromDomain(Notification notification) {
        return new NotificationResource(notification.id().toString(),
                Long.toString(notification.recipient().value()), notification.type(),
                notification.content().title(), notification.content().description(), "in_app",
                notification.status().name().toLowerCase(Locale.ROOT),
                notification.scheduledAt(), notification.sentAt(), notification.readAt(),
                notification.relatedEntity().type(), notification.relatedEntity().id(), notification.createdAt());
    }
}

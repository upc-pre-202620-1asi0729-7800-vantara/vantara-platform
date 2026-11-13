package com.vantara.platform.notifications.domain.model.valueobjects;

import java.util.UUID;

/**
 * Public identifier of a notification. It is a random UUID, so identifiers cannot be enumerated.
 */
public record NotificationId(UUID value) {
    public NotificationId {
        if (value == null) {
            throw new IllegalArgumentException("Notification id cannot be null");
        }
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID());
    }
}

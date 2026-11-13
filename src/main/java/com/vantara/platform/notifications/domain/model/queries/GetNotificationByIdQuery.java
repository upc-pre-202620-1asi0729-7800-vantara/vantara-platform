package com.vantara.platform.notifications.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve one notification of a user.
 *
 * @param notificationId identifier of the notification
 * @param recipientId    id of the user who owns it
 */
public record GetNotificationByIdQuery(UUID notificationId, Long recipientId) {
    public GetNotificationByIdQuery {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification id cannot be null");
        }
        if (recipientId == null || recipientId <= 0L) {
            throw new IllegalArgumentException("Recipient id cannot be null or less than 1");
        }
    }
}

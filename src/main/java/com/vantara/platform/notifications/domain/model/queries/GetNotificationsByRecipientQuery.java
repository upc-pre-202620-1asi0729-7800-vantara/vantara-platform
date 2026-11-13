package com.vantara.platform.notifications.domain.model.queries;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;

/**
 * Query to list the notifications of a user, newest first.
 *
 * @param recipientId id of the user who owns the notifications
 * @param type        kind to keep, or {@code null} for every kind
 * @param status      status to keep, or {@code null} for every status
 */
public record GetNotificationsByRecipientQuery(Long recipientId, NotificationType type, NotificationStatus status) {
    public GetNotificationsByRecipientQuery {
        if (recipientId == null || recipientId <= 0L) {
            throw new IllegalArgumentException("Recipient id cannot be null or less than 1");
        }
    }
}

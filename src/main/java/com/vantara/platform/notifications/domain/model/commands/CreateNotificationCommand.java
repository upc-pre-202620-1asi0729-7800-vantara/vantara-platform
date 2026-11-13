package com.vantara.platform.notifications.domain.model.commands;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;

/**
 * Command to notify a user ("create notification"). The notification is sent right away.
 *
 * @param recipientId       id of the user who receives the notification
 * @param type              kind of notification
 * @param title             short title
 * @param description       message
 * @param relatedEntityType kind of record the notification is about (for example Animal)
 * @param relatedEntityId   identifier of that record
 */
public record CreateNotificationCommand(Long recipientId, NotificationType type, String title,
                                        String description, String relatedEntityType, String relatedEntityId) {
    public CreateNotificationCommand {
        if (recipientId == null || recipientId <= 0L) {
            throw new IllegalArgumentException("Recipient id cannot be null or less than 1");
        }
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
    }
}

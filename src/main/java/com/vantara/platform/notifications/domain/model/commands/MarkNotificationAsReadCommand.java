package com.vantara.platform.notifications.domain.model.commands;

import java.util.UUID;

/**
 * Command to record that the recipient read a notification ("mark notification as read").
 *
 * @param notificationId identifier of the notification
 * @param recipientId    id of the user who owns it
 */
public record MarkNotificationAsReadCommand(UUID notificationId, Long recipientId) {
    public MarkNotificationAsReadCommand {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification id cannot be null");
        }
        if (recipientId == null || recipientId <= 0L) {
            throw new IllegalArgumentException("Recipient id cannot be null or less than 1");
        }
    }
}

package com.vantara.platform.notifications.application.commandservices;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.vantara.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;

/**
 * Write use cases of the notifications context.
 */
public interface NotificationCommandService {
    /**
     * @see CreateNotificationCommand
     * @return the stored notification, or a validation error when its content is invalid
     */
    Result<Notification, ApplicationError> handle(CreateNotificationCommand command);

    /**
     * @see MarkNotificationAsReadCommand
     * @return the notification with its read time, a not-found error when the user has no such
     *         notification, or a business rule violation while it is still scheduled
     */
    Result<Notification, ApplicationError> handle(MarkNotificationAsReadCommand command);
}

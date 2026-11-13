package com.vantara.platform.notifications.application.commandservices;

import com.vantara.platform.notifications.application.commands.*;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;

import java.util.Optional;

public interface NotificationCommandService {
    Notification handle(CreateNotificationCommand command);

    Optional<Notification> handle(MarkNotificationAsReadCommand command);
}

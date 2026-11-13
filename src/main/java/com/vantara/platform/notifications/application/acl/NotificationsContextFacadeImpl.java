package com.vantara.platform.notifications.application.acl;

import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.interfaces.acl.NotificationsContextFacade;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationsContextFacadeImpl implements NotificationsContextFacade {

    private final NotificationCommandService notificationCommandService;

    public NotificationsContextFacadeImpl(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @Override
    public Optional<String> createNotification(Long recipientUserId, String type, String title, String description,
                                               String relatedEntityType, String relatedEntityId) {
        CreateNotificationCommand command;
        try {
            command = new CreateNotificationCommand(recipientUserId, NotificationType.valueOf(type), title,
                    description, relatedEntityType, relatedEntityId);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
        var result = notificationCommandService.handle(command);
        if (result instanceof Result.Success<Notification, ?> success) {
            return Optional.of(success.value().getId().value().toString());
        }
        return Optional.empty();
    }
}

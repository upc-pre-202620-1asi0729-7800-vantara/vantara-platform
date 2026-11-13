package com.vantara.platform.notifications.application.internal.commandservices;

import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.vantara.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationContent;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationId;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import com.vantara.platform.notifications.domain.model.valueobjects.RelatedEntity;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final Clock clock;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository, Clock clock) {
        this.notificationRepository = notificationRepository;
        this.clock = clock;
    }

    /**
     * Builds the notification from the command values and stores it. The repository publishes the
     * created event after saving.
     */
    @Override
    @Transactional
    public Result<Notification, ApplicationError> handle(CreateNotificationCommand command) {
        Notification notification;
        try {
            notification = Notification.create(
                    new RecipientId(command.recipientId()),
                    command.type(),
                    new NotificationContent(command.title(), command.description()),
                    new RelatedEntity(command.relatedEntityType(), command.relatedEntityId()),
                    clock.instant(),
                    null);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("notification", e.getMessage()));
        }
        return Result.success(notificationRepository.save(notification));
    }

    /**
     * Reads the notification under a write lock, so concurrent requests keep the first read time.
     */
    @Override
    @Transactional
    public Result<Notification, ApplicationError> handle(MarkNotificationAsReadCommand command) {
        var notification = notificationRepository.findByIdForUpdate(
                new NotificationId(command.notificationId()), new RecipientId(command.recipientId()));
        if (notification.isEmpty()) {
            return Result.failure(ApplicationError.notFound("notification", command.notificationId().toString()));
        }
        try {
            notification.get().markAsRead(clock.instant());
        } catch (NotificationNotSentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("notification-not-sent", e.getMessage()));
        }
        return Result.success(notificationRepository.save(notification.get()));
    }
}

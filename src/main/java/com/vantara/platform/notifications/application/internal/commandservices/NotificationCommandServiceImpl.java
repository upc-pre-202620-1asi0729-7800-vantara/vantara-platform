package com.vantara.platform.notifications.application.internal.commandservices;

import com.vantara.platform.notifications.application.commands.*;
import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Service
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository repository;
    private final Clock clock;

    public NotificationCommandServiceImpl(NotificationRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Notification handle(CreateNotificationCommand command) {
        return repository.add(Notification.create(command.recipient(), command.type(),
                command.content(), command.relatedEntity(), clock.instant(), null));
    }

    @Override
    public Optional<Notification> handle(MarkNotificationAsReadCommand command) {
        return repository.markAsRead(command.id(), command.recipient(), clock.instant());
    }
}

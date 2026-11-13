package com.vantara.platform.notifications.application.internal.queryservices;

import com.vantara.platform.notifications.application.queries.*;
import com.vantara.platform.notifications.application.queryservices.NotificationQueryService;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationId;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository repository;

    public NotificationQueryServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Notification> handle(FindNotificationsQuery query) {
        return repository.findAll(query.recipient(), query.type(), query.status());
    }

    @Override
    public Optional<Notification> handle(FindNotificationByIdQuery query) {
        return repository.findById(new NotificationId(query.id()), query.recipient());
    }
}

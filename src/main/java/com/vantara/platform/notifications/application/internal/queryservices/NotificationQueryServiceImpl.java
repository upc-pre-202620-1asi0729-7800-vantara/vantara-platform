package com.vantara.platform.notifications.application.internal.queryservices;

import com.vantara.platform.notifications.application.queryservices.NotificationQueryService;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationsByRecipientQuery;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationId;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> handle(GetNotificationsByRecipientQuery query) {
        return notificationRepository.findAll(new RecipientId(query.recipientId()), query.type(), query.status());
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(
                new NotificationId(query.notificationId()), new RecipientId(query.recipientId()));
    }
}

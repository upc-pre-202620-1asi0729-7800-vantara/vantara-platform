package com.vantara.platform.notifications.application.queryservices;

import com.vantara.platform.notifications.application.queries.*;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    List<Notification> handle(FindNotificationsQuery query);

    Optional<Notification> handle(FindNotificationByIdQuery query);
}

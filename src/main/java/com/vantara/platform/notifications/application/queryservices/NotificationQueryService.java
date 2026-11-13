package com.vantara.platform.notifications.application.queryservices;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationsByRecipientQuery;

import java.util.List;
import java.util.Optional;

/**
 * Read use cases of the notifications context.
 */
public interface NotificationQueryService {
    /**
     * @see GetNotificationsByRecipientQuery
     */
    List<Notification> handle(GetNotificationsByRecipientQuery query);

    /**
     * @see GetNotificationByIdQuery
     */
    Optional<Notification> handle(GetNotificationByIdQuery query);
}

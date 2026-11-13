package com.vantara.platform.notifications.application.queries;

import com.vantara.platform.notifications.domain.model.valueobjects.*;

public record FindNotificationsQuery(RecipientId recipient, NotificationType type, NotificationStatus status) {
}

package com.vantara.platform.notifications.application.commands;

import com.vantara.platform.notifications.domain.model.valueobjects.*;

public record CreateNotificationCommand(RecipientId recipient, NotificationType type,
                                        NotificationContent content, RelatedEntity relatedEntity) {
}

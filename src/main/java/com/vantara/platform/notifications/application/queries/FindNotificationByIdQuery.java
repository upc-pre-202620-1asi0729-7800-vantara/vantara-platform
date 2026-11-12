package com.vantara.platform.notifications.application.queries;

import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import java.util.UUID;

public record FindNotificationByIdQuery(UUID id, RecipientId recipient) {
}

package com.vantara.platform.notifications.application.commands;

import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import java.util.UUID;

public record MarkNotificationAsReadCommand(UUID id, RecipientId recipient) {
}

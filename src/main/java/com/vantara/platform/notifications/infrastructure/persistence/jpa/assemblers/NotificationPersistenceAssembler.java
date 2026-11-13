package com.vantara.platform.notifications.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;

/**
 * Translates between the {@link Notification} aggregate and its persistence entity.
 */
public final class NotificationPersistenceAssembler {
    private NotificationPersistenceAssembler() {
    }

    public static Notification toDomainFromPersistence(NotificationPersistenceEntity entity) {
        return Notification.reconstitute(new NotificationId(entity.getId()),
                new RecipientId(entity.getRecipientUserId()), entity.getType(),
                new NotificationContent(entity.getTitle(), entity.getDescription()),
                new RelatedEntity(entity.getRelatedEntityType(), entity.getRelatedEntityId()),
                entity.getCreatedAt(), entity.getScheduledAt(), entity.getSentAt(), entity.getReadAt());
    }

    public static NotificationPersistenceEntity toPersistenceFromDomain(
            NotificationPersistenceEntity target, Notification notification) {
        target.setId(notification.getId().value());
        target.setRecipientUserId(notification.getRecipient().value());
        target.setType(notification.getType());
        target.setTitle(notification.getContent().title());
        target.setDescription(notification.getContent().description());
        target.setRelatedEntityType(notification.getRelatedEntity().type());
        target.setRelatedEntityId(notification.getRelatedEntity().id());
        target.setCreatedAt(notification.getCreatedAt());
        target.setScheduledAt(notification.getScheduledAt());
        target.setSentAt(notification.getSentAt());
        target.setReadAt(notification.getReadAt());
        return target;
    }
}

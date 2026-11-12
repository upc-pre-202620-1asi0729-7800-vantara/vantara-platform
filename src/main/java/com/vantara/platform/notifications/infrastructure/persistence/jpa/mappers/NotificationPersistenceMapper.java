package com.vantara.platform.notifications.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationJpaEntity;

public final class NotificationPersistenceMapper {
    private NotificationPersistenceMapper() {
    }

    public static NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(notification.id(), notification.recipient().value(),
                notification.type(), notification.content().title(), notification.content().description(),
                notification.relatedEntity().type(), notification.relatedEntity().id(), notification.createdAt(),
                notification.scheduledAt(), notification.sentAt(), notification.readAt());
    }

    public static Notification toDomain(NotificationJpaEntity entity) {
        return Notification.reconstitute(entity.getId(), new RecipientId(entity.getRecipientUserId()),
                entity.getType(), new NotificationContent(entity.getTitle(), entity.getDescription()),
                new RelatedEntity(entity.getRelatedEntityType(), entity.getRelatedEntityId()),
                entity.getCreatedAt(), entity.getScheduledAt(), entity.getSentAt(), entity.getReadAt());
    }
}

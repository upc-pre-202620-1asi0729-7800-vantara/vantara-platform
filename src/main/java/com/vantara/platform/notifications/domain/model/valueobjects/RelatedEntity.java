package com.vantara.platform.notifications.domain.model.valueobjects;

public record RelatedEntity(String type, String id) {
    public RelatedEntity {
        type = NotificationContent.requireText(type, 64, "Related entity type");
        id = NotificationContent.requireText(id, 128, "Related entity id");
    }
}

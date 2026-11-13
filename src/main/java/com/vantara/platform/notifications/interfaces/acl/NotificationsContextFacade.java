package com.vantara.platform.notifications.interfaces.acl;

import java.util.Optional;

/**
 * Anti-corruption facade that exposes the notifications capabilities other bounded contexts may use.
 * It only handles primitive values, so no notifications domain class leaks out of the context.
 */
public interface NotificationsContextFacade {

    /**
     * Notifies a user. The notification is sent right away.
     *
     * @param recipientUserId   id of the user who receives the notification
     * @param type              name of the notification type (for example {@code HEALTH_ALERT})
     * @param title             short title
     * @param description       message
     * @param relatedEntityType kind of record the notification is about (for example {@code Animal})
     * @param relatedEntityId   identifier of that record
     * @return the id of the new notification, or empty when the data is invalid
     */
    Optional<String> createNotification(Long recipientUserId, String type, String title, String description,
                                        String relatedEntityType, String relatedEntityId);
}

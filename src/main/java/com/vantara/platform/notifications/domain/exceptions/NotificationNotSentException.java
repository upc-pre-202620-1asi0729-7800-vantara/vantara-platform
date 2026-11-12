package com.vantara.platform.notifications.domain.exceptions;

public class NotificationNotSentException extends IllegalStateException {
    public NotificationNotSentException() {
        super("Only sent notifications can be marked as read");
    }
}

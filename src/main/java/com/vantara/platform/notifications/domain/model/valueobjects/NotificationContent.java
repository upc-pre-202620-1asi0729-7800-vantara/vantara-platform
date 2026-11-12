package com.vantara.platform.notifications.domain.model.valueobjects;

public record NotificationContent(String title, String description) {
    public NotificationContent {
        title = requireText(title, 160, "Title");
        description = requireText(description, 2000, "Description");
    }

    static String requireText(String value, int maxLength, String field) {
        if (value == null || value.isBlank() || value.strip().length() > maxLength) {
            throw new IllegalArgumentException(field + " must contain 1 to " + maxLength + " characters");
        }
        return value.strip();
    }
}

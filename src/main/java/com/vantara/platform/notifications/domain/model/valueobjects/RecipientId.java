package com.vantara.platform.notifications.domain.model.valueobjects;

public record RecipientId(long value) {
    public RecipientId {
        if (value <= 0) {
            throw new IllegalArgumentException("Recipient id must be positive");
        }
    }
}

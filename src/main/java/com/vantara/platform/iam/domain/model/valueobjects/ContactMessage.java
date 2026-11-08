package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Message written in the contact form.
 *
 * @param content the message text
 */
public record ContactMessage(String content) {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 1000;

    public ContactMessage {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Contact message cannot be null or blank");
        }
        content = content.trim();
        if (content.length() < MIN_LENGTH || content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Contact message length must be between %d and %d characters".formatted(MIN_LENGTH, MAX_LENGTH));
        }
    }
}

package com.vantara.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email address of an account holder or a contact requester.
 *
 * <p>The address is normalized (trimmed and lower-cased) so equality is case-insensitive.</p>
 *
 * @param address the normalized email address
 */
public record EmailAddress(String address) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_LENGTH = 254;

    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }
        address = address.trim().toLowerCase(Locale.ROOT);
        if (address.length() > MAX_LENGTH || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Email address format is invalid");
        }
    }
}

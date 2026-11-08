package com.vantara.platform.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Numeric code sent by email to verify an account or to recover a password.
 *
 * @param value the six-digit code
 */
public record VerificationCodeValue(String value) {

    private static final Pattern SIX_DIGITS = Pattern.compile("^\\d{6}$");

    public VerificationCodeValue {
        if (value == null || !SIX_DIGITS.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Verification code must be exactly 6 digits");
        }
        value = value.trim();
    }
}

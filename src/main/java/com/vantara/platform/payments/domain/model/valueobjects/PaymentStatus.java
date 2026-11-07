package com.vantara.platform.payments.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Lifecycle state of a payment.
 */
public enum PaymentStatus {

    PENDING("pending"),
    PAID("paid"),
    CANCELLED("cancelled"),
    FAILED("failed");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    /**
     * @return the value exposed by the REST API
     */
    public String value() {
        return value;
    }

    /**
     * Resolves a status from its API value.
     *
     * @param value API value
     * @return matching status
     */
    public static PaymentStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("payment.error.status.required");
        }
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("payment.error.status.invalid"));
    }
}

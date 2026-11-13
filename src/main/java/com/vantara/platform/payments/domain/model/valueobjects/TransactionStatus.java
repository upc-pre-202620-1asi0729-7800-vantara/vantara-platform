package com.vantara.platform.payments.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Outcome of a payment transaction attempt.
 */
public enum TransactionStatus {

    PENDING("pending"),
    APPROVED("approved"),
    DECLINED("declined");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    /**
     * @return the value exposed by the REST API
     */
    public String value() {
        return value;
    }

    /**
     * Resolves a transaction status from its API value.
     *
     * @param value API value
     * @return matching status
     */
    public static TransactionStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("payment.error.transactionStatus.required");
        }
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("payment.error.transactionStatus.invalid"));
    }
}

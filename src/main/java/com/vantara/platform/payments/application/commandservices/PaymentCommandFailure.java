package com.vantara.platform.payments.application.commandservices;

/**
 * Failure produced while handling a payment command.
 */
public sealed interface PaymentCommandFailure permits PaymentCommandFailure.Validation {

    /**
     * @return localized message key describing the failure
     */
    String messageKey();

    /**
     * Input or domain validation failure.
     *
     * @param messageKey localized message key
     */
    record Validation(String messageKey) implements PaymentCommandFailure {
    }
}

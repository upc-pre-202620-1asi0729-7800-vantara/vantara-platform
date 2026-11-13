package com.vantara.platform.payments.application.commands;

import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;

import java.math.BigDecimal;

/**
 * Command that requests the creation of a payment.
 *
 * @param rancherId     owner of the payment
 * @param appointmentId optional appointment reference
 * @param concept       payment concept
 * @param amount        amount to charge
 * @param currency      ISO-4217 currency code
 * @param paymentMethod optional non-sensitive payment method metadata
 */
public record CreatePaymentCommand(String rancherId,
                                   String appointmentId,
                                   String concept,
                                   BigDecimal amount,
                                   String currency,
                                   PaymentMethod paymentMethod) {
}

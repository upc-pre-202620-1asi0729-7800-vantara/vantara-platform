package com.vantara.platform.payments.application.ports;

import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;

import java.time.Instant;

/**
 * Outbound port for payment gateways.
 */
public interface PaymentGateway {

    /**
     * Charges a payment.
     *
     * @param payment       payment to charge
     * @param paymentMethod optional payment method metadata
     * @return gateway result
     */
    PaymentGatewayResult charge(Payment payment, PaymentMethod paymentMethod);

    /**
     * Result of a gateway charge attempt.
     *
     * @param provider       gateway provider name
     * @param reference      gateway reference
     * @param approved       whether the charge was approved
     * @param failureReason  optional failure reason
     * @param attemptedAt    attempt instant
     */
    record PaymentGatewayResult(String provider,
                                String reference,
                                boolean approved,
                                String failureReason,
                                Instant attemptedAt) {
    }
}

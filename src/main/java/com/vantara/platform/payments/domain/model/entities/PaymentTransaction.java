package com.vantara.platform.payments.domain.model.entities;

import com.vantara.platform.payments.domain.model.valueobjects.TransactionStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A single attempt to charge a payment through a gateway.
 */
public final class PaymentTransaction {

    private final String id;
    private final String paymentId;
    private final String provider;
    private final String providerReference;
    private final TransactionStatus status;
    private final Instant attemptedAt;
    private final String failureReason;

    private PaymentTransaction(String id,
                               String paymentId,
                               String provider,
                               String providerReference,
                               TransactionStatus status,
                               Instant attemptedAt,
                               String failureReason) {
        this.id = id;
        this.paymentId = paymentId;
        this.provider = provider;
        this.providerReference = providerReference;
        this.status = status;
        this.attemptedAt = attemptedAt;
        this.failureReason = failureReason;
    }

    /**
     * Creates a new transaction attempt.
     *
     * @param paymentId        owning payment identifier
     * @param provider         gateway provider name
     * @param providerReference optional gateway reference
     * @param status           transaction status
     * @param attemptedAt      attempt instant
     * @param failureReason    optional failure reason
     * @return new transaction
     */
    public static PaymentTransaction create(String paymentId,
                                            String provider,
                                            String providerReference,
                                            TransactionStatus status,
                                            Instant attemptedAt,
                                            String failureReason) {
        return new PaymentTransaction(
                UUID.randomUUID().toString(),
                requireText(paymentId, "payment.error.transaction.paymentId.required"),
                requireText(provider, "payment.error.transaction.provider.required"),
                normalizeOptionalText(providerReference),
                Objects.requireNonNull(status, "payment.error.transactionStatus.required"),
                Objects.requireNonNull(attemptedAt, "payment.error.transaction.attemptedAt.required"),
                normalizeOptionalText(failureReason)
        );
    }

    /**
     * Rehydrates a transaction from persistence.
     *
     * @param id               transaction identifier
     * @param paymentId        owning payment identifier
     * @param provider         gateway provider name
     * @param providerReference optional gateway reference
     * @param status           transaction status
     * @param attemptedAt      attempt instant
     * @param failureReason    optional failure reason
     * @return rehydrated transaction
     */
    public static PaymentTransaction rehydrate(String id,
                                               String paymentId,
                                               String provider,
                                               String providerReference,
                                               TransactionStatus status,
                                               Instant attemptedAt,
                                               String failureReason) {
        return new PaymentTransaction(
                requireText(id, "payment.error.transaction.id.required"),
                requireText(paymentId, "payment.error.transaction.paymentId.required"),
                requireText(provider, "payment.error.transaction.provider.required"),
                normalizeOptionalText(providerReference),
                Objects.requireNonNull(status, "payment.error.transactionStatus.required"),
                Objects.requireNonNull(attemptedAt, "payment.error.transaction.attemptedAt.required"),
                normalizeOptionalText(failureReason)
        );
    }

    private static String requireText(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }
}

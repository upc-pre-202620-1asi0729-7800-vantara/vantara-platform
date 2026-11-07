package com.vantara.platform.payments.infrastructure.persistence.jpa.entities;

import com.vantara.platform.payments.domain.model.valueobjects.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA representation of a payment transaction.
 */
@Entity
@Table(name = "payment_transaction", indexes = {
        @Index(name = "idx_payment_transaction_payment", columnList = "payment_id")
})
public class PaymentTransactionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "payment_id", nullable = false, length = 36)
    private String paymentId;

    @Column(name = "provider", nullable = false, length = 60)
    private String provider;

    @Column(name = "provider_reference", length = 120)
    private String providerReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    protected PaymentTransactionJpaEntity() {
    }

    public PaymentTransactionJpaEntity(String id,
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

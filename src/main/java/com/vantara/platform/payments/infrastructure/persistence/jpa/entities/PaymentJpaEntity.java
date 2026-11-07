package com.vantara.platform.payments.infrastructure.persistence.jpa.entities;

import com.vantara.platform.payments.domain.model.valueobjects.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA representation of a payment.
 */
@Entity
@Table(name = "payment_payment", indexes = {
        @Index(name = "idx_payment_payment_rancher", columnList = "rancher_id"),
        @Index(name = "idx_payment_payment_created_at", columnList = "created_at")
})
public class PaymentJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "rancher_id", nullable = false, length = 36)
    private String rancherId;

    @Column(name = "appointment_id", length = 36)
    private String appointmentId;

    @Column(name = "concept", nullable = false, length = 200)
    private String concept;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "payment_method_brand", length = 20)
    private String paymentMethodBrand;

    @Column(name = "payment_method_last4", length = 4)
    private String paymentMethodLast4;

    @Column(name = "payment_method_holder", length = 160)
    private String paymentMethodHolder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected PaymentJpaEntity() {
    }

    public PaymentJpaEntity(String id,
                            String rancherId,
                            String appointmentId,
                            String concept,
                            BigDecimal amount,
                            String currency,
                            PaymentStatus status,
                            String paymentMethodBrand,
                            String paymentMethodLast4,
                            String paymentMethodHolder,
                            Instant createdAt,
                            Instant confirmedAt,
                            Instant cancelledAt) {
        this.id = id;
        this.rancherId = rancherId;
        this.appointmentId = appointmentId;
        this.concept = concept;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethodBrand = paymentMethodBrand;
        this.paymentMethodLast4 = paymentMethodLast4;
        this.paymentMethodHolder = paymentMethodHolder;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    public String getId() {
        return id;
    }

    public String getRancherId() {
        return rancherId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getConcept() {
        return concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentMethodBrand() {
        return paymentMethodBrand;
    }

    public String getPaymentMethodLast4() {
        return paymentMethodLast4;
    }

    public String getPaymentMethodHolder() {
        return paymentMethodHolder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }
}

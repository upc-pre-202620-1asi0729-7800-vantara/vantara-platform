package com.vantara.platform.payments.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * JPA representation of a payment receipt.
 */
@Entity
@Table(name = "payment_receipt",
        uniqueConstraints = @UniqueConstraint(name = "uk_payment_receipt_number", columnNames = "receipt_number"),
        indexes = @Index(name = "idx_payment_receipt_payment", columnList = "payment_id"))
public class PaymentReceiptJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "payment_id", nullable = false, length = 36)
    private String paymentId;

    @Column(name = "receipt_number", nullable = false, length = 40)
    private String receiptNumber;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "document_url", nullable = false, length = 500)
    private String documentUrl;

    protected PaymentReceiptJpaEntity() {
    }

    public PaymentReceiptJpaEntity(String id,
                                   String paymentId,
                                   String receiptNumber,
                                   Instant issuedAt,
                                   String documentUrl) {
        this.id = id;
        this.paymentId = paymentId;
        this.receiptNumber = receiptNumber;
        this.issuedAt = issuedAt;
        this.documentUrl = documentUrl;
    }

    public String getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }
}

package com.vantara.platform.payments.domain.model.entities;

import java.time.Instant;
import java.util.Objects;

/**
 * Receipt issued after a successful payment.
 */
public final class PaymentReceipt {

    private final String id;
    private final String paymentId;
    private final String receiptNumber;
    private final Instant issuedAt;
    private final String documentUrl;

    private PaymentReceipt(String id,
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

    /**
     * Creates a receipt with a caller-provided identifier so the document URL
     * can be built without leaking HTTP concerns into the domain.
     *
     * @param id            receipt identifier
     * @param paymentId     owning payment identifier
     * @param receiptNumber human-readable receipt number
     * @param issuedAt      issue instant
     * @param documentUrl   URL of the downloadable document
     * @return new receipt
     */
    public static PaymentReceipt create(String id,
                                        String paymentId,
                                        String receiptNumber,
                                        Instant issuedAt,
                                        String documentUrl) {
        return new PaymentReceipt(
                requireText(id, "payment.error.receipt.id.required"),
                requireText(paymentId, "payment.error.receipt.paymentId.required"),
                requireText(receiptNumber, "payment.error.receipt.number.required"),
                Objects.requireNonNull(issuedAt, "payment.error.receipt.issuedAt.required"),
                requireText(documentUrl, "payment.error.receipt.documentUrl.required")
        );
    }

    /**
     * Rehydrates a receipt from persistence.
     *
     * @param id            receipt identifier
     * @param paymentId     owning payment identifier
     * @param receiptNumber human-readable receipt number
     * @param issuedAt      issue instant
     * @param documentUrl   URL of the downloadable document
     * @return rehydrated receipt
     */
    public static PaymentReceipt rehydrate(String id,
                                           String paymentId,
                                           String receiptNumber,
                                           Instant issuedAt,
                                           String documentUrl) {
        return create(id, paymentId, receiptNumber, issuedAt, documentUrl);
    }

    private static String requireText(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
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

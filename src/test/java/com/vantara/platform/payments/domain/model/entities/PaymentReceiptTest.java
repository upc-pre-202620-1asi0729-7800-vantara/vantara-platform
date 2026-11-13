package com.vantara.platform.payments.domain.model.entities;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentReceiptTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");

    @Test
    void createsReceiptWithDocumentUrl() {
        var receipt = PaymentReceipt.create(
                "rec-1",
                "pay-1",
                "B001-000001",
                NOW,
                "/api/v1/receipts/rec-1/document"
        );

        assertThat(receipt.getId()).isEqualTo("rec-1");
        assertThat(receipt.getPaymentId()).isEqualTo("pay-1");
        assertThat(receipt.getReceiptNumber()).isEqualTo("B001-000001");
        assertThat(receipt.getIssuedAt()).isEqualTo(NOW);
        assertThat(receipt.getDocumentUrl()).isEqualTo("/api/v1/receipts/rec-1/document");
    }

    @Test
    void rejectsMissingDocumentUrl() {
        assertThatThrownBy(() -> PaymentReceipt.create(
                "rec-1",
                "pay-1",
                "B001-000001",
                NOW,
                " "
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("payment.error.receipt.documentUrl.required");
    }
}

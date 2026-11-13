package com.vantara.platform.payments.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Payment receipt response payload.
 */
@Schema(name = "PaymentReceipt", description = "Payment receipt resource")
public record PaymentReceiptResource(
        @Schema(description = "Receipt identifier")
        String id,
        @Schema(description = "Owning payment identifier")
        String paymentId,
        @Schema(description = "Human-readable receipt number", example = "B001-000001")
        String receiptNumber,
        @Schema(description = "Issue instant")
        Instant issuedAt,
        @Schema(description = "URL of the PDF document", example = "/api/v1/receipts/abc/document")
        String documentUrl
) {
}

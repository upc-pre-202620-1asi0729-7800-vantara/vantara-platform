package com.vantara.platform.payments.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Payment transaction response payload.
 */
@Schema(name = "PaymentTransaction", description = "Payment transaction resource")
public record PaymentTransactionResource(
        @Schema(description = "Transaction identifier")
        String id,
        @Schema(description = "Owning payment identifier")
        String paymentId,
        @Schema(description = "Gateway provider", example = "FakePay")
        String provider,
        @Schema(description = "Gateway reference", example = "FP-20261107120000-ABCD1234")
        String providerReference,
        @Schema(description = "Transaction status", example = "approved")
        String status,
        @Schema(description = "Attempt instant")
        Instant attemptedAt,
        @Schema(description = "Failure reason when declined")
        String failureReason
) {
}

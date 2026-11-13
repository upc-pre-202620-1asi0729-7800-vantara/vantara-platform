package com.vantara.platform.payments.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment response payload.
 */
@Schema(name = "Payment", description = "Payment resource")
public record PaymentResource(
        @Schema(description = "Payment identifier", example = "9f1c5e7d-5d3a-4a5e-9c3b-3a0f5f4d2e11")
        String id,
        @Schema(description = "Rancher identifier", example = "usr-002")
        String rancherId,
        @Schema(description = "Optional appointment identifier", example = "app-001")
        String appointmentId,
        @Schema(description = "Payment concept", example = "Suscripción Profesional")
        String concept,
        @Schema(description = "Amount", example = "49.90")
        BigDecimal amount,
        @Schema(description = "ISO-4217 currency code", example = "PEN")
        String currency,
        @Schema(description = "Payment status", example = "paid")
        String status,
        @Schema(description = "Creation instant")
        Instant createdAt,
        @Schema(description = "Confirmation instant")
        Instant confirmedAt,
        @Schema(description = "Cancellation instant")
        Instant cancelledAt,
        @Schema(description = "Optional non-sensitive payment method metadata")
        PaymentMethodResource paymentMethod
) {
}

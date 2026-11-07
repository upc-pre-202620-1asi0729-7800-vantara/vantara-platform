package com.vantara.platform.payments.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Non-sensitive payment method metadata accepted by the API.
 *
 * @param brand      card brand
 * @param last4      last four digits of the card
 * @param holderName optional card holder name
 */
@Schema(name = "PaymentMethod", description = "Non-sensitive payment method metadata")
public record PaymentMethodResource(
        @Schema(description = "Card brand", example = "VISA", maxLength = 20)
        String brand,
        @Schema(description = "Last four digits of the card", example = "4582", pattern = "\\d{4}")
        String last4,
        @Schema(description = "Card holder name", example = "JUAN QUISPE", maxLength = 160)
        String holderName
) {
}

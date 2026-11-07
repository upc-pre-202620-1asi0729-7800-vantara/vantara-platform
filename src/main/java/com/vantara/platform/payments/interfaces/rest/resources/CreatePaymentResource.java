package com.vantara.platform.payments.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request payload for creating a payment.
 *
 * <p>Client-provided status and timestamp fields are ignored; the backend
 * derives them from the simulated payment flow.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CreatePaymentRequest", description = "Payment creation request")
public record CreatePaymentResource(
        @Schema(description = "Rancher identifier", example = "usr-002", maxLength = 36)
        @NotBlank(message = "{payment.error.rancherId.required}")
        @Size(max = 36, message = "{payment.error.rancherId.invalid}")
        String rancherId,

        @Schema(description = "Optional appointment identifier", example = "app-001", maxLength = 36)
        @Size(max = 36, message = "{payment.error.appointmentId.invalid}")
        String appointmentId,

        @Schema(description = "Payment concept", example = "Suscripción Profesional", maxLength = 200)
        @NotBlank(message = "{payment.error.concept.required}")
        @Size(max = 200, message = "{payment.error.concept.invalid}")
        String concept,

        @Schema(description = "Amount to charge", example = "49.90")
        @NotNull(message = "{payment.error.amount.required}")
        @DecimalMin(value = "0.01", message = "{payment.error.amount.positive}")
        BigDecimal amount,

        @Schema(description = "ISO-4217 currency code", example = "PEN", minLength = 3, maxLength = 3)
        @NotBlank(message = "{payment.error.currency.required}")
        @Pattern(regexp = "[A-Za-z]{3}", message = "{payment.error.currency.invalid}")
        String currency,

        @Schema(description = "Optional non-sensitive payment method metadata")
        @Valid
        PaymentMethodResource paymentMethod
) {
}

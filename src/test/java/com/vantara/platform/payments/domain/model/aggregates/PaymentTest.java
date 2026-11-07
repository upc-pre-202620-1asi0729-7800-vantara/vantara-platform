package com.vantara.platform.payments.domain.model.aggregates;

import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");

    @Test
    void createsPaidPaymentWithAppointmentAndPaymentMethod() {
        var method = PaymentMethod.of("visa", "4582", "  JUAN QUISPE  ");

        var payment = Payment.create(
                " usr-002 ",
                " app-001 ",
                " Control veterinario ",
                new BigDecimal("150"),
                "pen",
                method,
                NOW
        );

        assertThat(payment.getId()).isNotBlank();
        assertThat(payment.getRancherId()).isEqualTo("usr-002");
        assertThat(payment.getAppointmentId()).isEqualTo("app-001");
        assertThat(payment.getConcept()).isEqualTo("Control veterinario");
        assertThat(payment.getAmount()).isEqualByComparingTo("150.00");
        assertThat(payment.getCurrency()).isEqualTo("PEN");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.of("VISA", "4582", "JUAN QUISPE"));
        assertThat(payment.getCreatedAt()).isEqualTo(NOW);
        assertThat(payment.getConfirmedAt()).isEqualTo(NOW);
        assertThat(payment.getCancelledAt()).isNull();
    }

    @Test
    void createsSubscriptionPaymentWithoutAppointment() {
        var payment = Payment.create(
                "usr-002",
                "  ",
                "Suscripción Profesional",
                new BigDecimal("49.90"),
                "PEN",
                null,
                NOW
        );

        assertThat(payment.getAppointmentId()).isNull();
        assertThat(payment.getPaymentMethod()).isNull();
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> Payment.create(
                "usr-002",
                null,
                "Suscripción Profesional",
                BigDecimal.ZERO,
                "PEN",
                null,
                NOW
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("payment.error.amount.positive");
    }

    @Test
    void rejectsInvalidCurrency() {
        assertThatThrownBy(() -> Payment.create(
                "usr-002",
                null,
                "Suscripción Profesional",
                new BigDecimal("49.90"),
                "PEN1",
                null,
                NOW
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("payment.error.currency.invalid");
    }
}

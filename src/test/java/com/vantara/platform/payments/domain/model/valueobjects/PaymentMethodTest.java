package com.vantara.platform.payments.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentMethodTest {

    @Test
    void normalizesBrandAndTrimsHolderName() {
        var method = PaymentMethod.of(" visa ", "4582", "  JUAN QUISPE  ");

        assertThat(method.brand()).isEqualTo("VISA");
        assertThat(method.last4()).isEqualTo("4582");
        assertThat(method.holderName()).isEqualTo("JUAN QUISPE");
        assertThat(method.masked()).isEqualTo("VISA **** 4582");
    }

    @Test
    void rejectsInvalidLastFourDigits() {
        assertThatThrownBy(() -> PaymentMethod.of("VISA", "45A2", "JUAN QUISPE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("payment.error.paymentMethod.last4.invalid");
    }

    @Test
    void rejectsMissingBrand() {
        assertThatThrownBy(() -> PaymentMethod.of(" ", "4582", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("payment.error.paymentMethod.brand.required");
    }
}

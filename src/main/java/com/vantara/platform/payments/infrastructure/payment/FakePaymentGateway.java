package com.vantara.platform.payments.infrastructure.payment;

import com.vantara.platform.payments.application.ports.PaymentGateway;
import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Fake gateway used by the university checkout flow. Every charge is approved.
 */
@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final DateTimeFormatter REFERENCE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    private final Clock clock;

    public FakePaymentGateway(Clock clock) {
        this.clock = clock;
    }

    @Override
    public PaymentGatewayResult charge(Payment payment, PaymentMethod paymentMethod) {
        var now = clock.instant();
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        var reference = "FP-" + REFERENCE_FORMATTER.format(now) + "-" + suffix;
        return new PaymentGatewayResult("FakePay", reference, true, null, now);
    }
}

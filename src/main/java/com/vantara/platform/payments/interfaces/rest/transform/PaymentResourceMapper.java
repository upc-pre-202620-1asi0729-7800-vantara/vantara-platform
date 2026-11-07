package com.vantara.platform.payments.interfaces.rest.transform;

import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentMethodResource;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps payments to HTTP response resources.
 */
@Component
public class PaymentResourceMapper {

    public PaymentResource toResource(Payment payment) {
        var method = payment.getPaymentMethod();
        var methodResource = method == null
                ? null
                : new PaymentMethodResource(method.brand(), method.last4(), method.holderName());

        return new PaymentResource(
                payment.getId(),
                payment.getRancherId(),
                payment.getAppointmentId(),
                payment.getConcept(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().value(),
                payment.getCreatedAt(),
                payment.getConfirmedAt(),
                payment.getCancelledAt(),
                methodResource
        );
    }

    public List<PaymentResource> toResources(List<Payment> payments) {
        return payments.stream().map(this::toResource).toList();
    }
}

package com.vantara.platform.payments.interfaces.rest.transform;

import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentTransactionResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps payment transactions to HTTP response resources.
 */
@Component
public class PaymentTransactionResourceMapper {

    public PaymentTransactionResource toResource(PaymentTransaction transaction) {
        return new PaymentTransactionResource(
                transaction.getId(),
                transaction.getPaymentId(),
                transaction.getProvider(),
                transaction.getProviderReference(),
                transaction.getStatus().value(),
                transaction.getAttemptedAt(),
                transaction.getFailureReason()
        );
    }

    public List<PaymentTransactionResource> toResources(List<PaymentTransaction> transactions) {
        return transactions.stream().map(this::toResource).toList();
    }
}

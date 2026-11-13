package com.vantara.platform.payments.application.queryservices;

import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;

import java.util.List;

/**
 * Inbound port for payment transaction queries.
 */
public interface PaymentTransactionQueryService {

    /**
     * Returns the transactions of a payment ordered by attempt instant.
     *
     * @param paymentId payment identifier
     * @return matching transactions, possibly empty
     */
    List<PaymentTransaction> findByPaymentId(String paymentId);
}

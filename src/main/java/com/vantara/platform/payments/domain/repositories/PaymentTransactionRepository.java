package com.vantara.platform.payments.domain.repositories;

import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;

import java.util.List;

/**
 * Outbound port for payment transaction persistence.
 */
public interface PaymentTransactionRepository {

    /**
     * Persists a transaction.
     *
     * @param transaction transaction to save
     * @return persisted transaction
     */
    PaymentTransaction save(PaymentTransaction transaction);

    /**
     * Returns the transactions of a payment ordered by attempt instant.
     *
     * @param paymentId payment identifier
     * @return matching transactions
     */
    List<PaymentTransaction> findByPaymentId(String paymentId);
}

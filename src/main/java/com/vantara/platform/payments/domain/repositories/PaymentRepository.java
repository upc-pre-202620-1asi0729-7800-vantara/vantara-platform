package com.vantara.platform.payments.domain.repositories;

import com.vantara.platform.payments.domain.model.aggregates.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for payment persistence.
 */
public interface PaymentRepository {

    /**
     * Persists a payment.
     *
     * @param payment payment to save
     * @return persisted payment
     */
    Payment save(Payment payment);

    /**
     * Finds a payment by identifier.
     *
     * @param id payment identifier
     * @return matching payment, if any
     */
    Optional<Payment> findById(String id);

    /**
     * Returns all payments ordered by creation instant ascending.
     *
     * @return ordered payments
     */
    List<Payment> findAllOrderByCreatedAtAsc();
}

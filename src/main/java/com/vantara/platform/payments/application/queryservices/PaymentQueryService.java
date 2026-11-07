package com.vantara.platform.payments.application.queryservices;

import com.vantara.platform.payments.domain.model.aggregates.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port for payment queries.
 */
public interface PaymentQueryService {

    /**
     * Returns all payments ordered by creation instant ascending.
     *
     * @return ordered payments
     */
    List<Payment> findAll();

    /**
     * Finds a payment by identifier.
     *
     * @param id payment identifier
     * @return matching payment, if any
     */
    Optional<Payment> findById(String id);
}

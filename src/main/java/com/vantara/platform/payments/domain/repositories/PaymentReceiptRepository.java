package com.vantara.platform.payments.domain.repositories;

import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for payment receipt persistence.
 */
public interface PaymentReceiptRepository {

    /**
     * Persists a receipt.
     *
     * @param receipt receipt to save
     * @return persisted receipt
     */
    PaymentReceipt save(PaymentReceipt receipt);

    /**
     * Finds a receipt by identifier.
     *
     * @param id receipt identifier
     * @return matching receipt, if any
     */
    Optional<PaymentReceipt> findById(String id);

    /**
     * Returns the receipts of a payment ordered by issue instant.
     *
     * @param paymentId payment identifier
     * @return matching receipts
     */
    List<PaymentReceipt> findByPaymentId(String paymentId);

    /**
     * Returns the number of stored receipts.
     *
     * @return receipt count
     */
    long count();
}

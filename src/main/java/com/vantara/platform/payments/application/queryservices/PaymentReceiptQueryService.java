package com.vantara.platform.payments.application.queryservices;

import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port for payment receipt queries.
 */
public interface PaymentReceiptQueryService {

    /**
     * Returns the receipts of a payment ordered by issue instant.
     *
     * @param paymentId payment identifier
     * @return matching receipts, possibly empty
     */
    List<PaymentReceipt> findByPaymentId(String paymentId);

    /**
     * Renders the PDF document of a receipt.
     *
     * @param receiptId receipt identifier
     * @return PDF bytes, if the receipt and its payment exist
     */
    Optional<byte[]> renderDocument(String receiptId);
}

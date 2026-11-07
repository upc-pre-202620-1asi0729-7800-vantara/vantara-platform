package com.vantara.platform.payments.application.ports;

import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;

/**
 * Outbound port that renders receipt documents.
 */
public interface ReceiptDocumentGenerator {

    /**
     * Renders a receipt as a PDF document.
     *
     * @param payment payment that originated the receipt
     * @param receipt receipt to render
     * @return PDF bytes
     */
    byte[] generate(Payment payment, PaymentReceipt receipt);
}

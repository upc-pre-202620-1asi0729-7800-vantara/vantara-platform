package com.vantara.platform.payments.application.ports;

/**
 * Outbound port that produces human-readable receipt numbers.
 */
public interface ReceiptNumberGenerator {

    /**
     * @return the next receipt number
     */
    String nextReceiptNumber();
}

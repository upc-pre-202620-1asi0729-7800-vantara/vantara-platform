package com.vantara.platform.payments.infrastructure.receipt;

import com.vantara.platform.payments.application.ports.ReceiptNumberGenerator;
import com.vantara.platform.payments.domain.repositories.PaymentReceiptRepository;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Generates sequential receipt numbers for the current database.
 *
 * <p>This is intentionally simple for the university project. The unique
 * constraint on {@code receipt_number} still protects against duplicates.</p>
 */
@Component
public class SequentialReceiptNumberGenerator implements ReceiptNumberGenerator {

    private final PaymentReceiptRepository receiptRepository;

    public SequentialReceiptNumberGenerator(PaymentReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public String nextReceiptNumber() {
        return String.format(Locale.ROOT, "B001-%06d", receiptRepository.count() + 1);
    }
}

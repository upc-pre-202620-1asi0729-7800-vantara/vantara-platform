package com.vantara.platform.payments.application.internal.queryservices;

import com.vantara.platform.payments.application.ports.ReceiptDocumentGenerator;
import com.vantara.platform.payments.application.queryservices.PaymentReceiptQueryService;
import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import com.vantara.platform.payments.domain.repositories.PaymentReceiptRepository;
import com.vantara.platform.payments.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default payment receipt query service.
 */
@Service
public class PaymentReceiptQueryServiceImpl implements PaymentReceiptQueryService {

    private final PaymentReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptDocumentGenerator documentGenerator;

    public PaymentReceiptQueryServiceImpl(PaymentReceiptRepository receiptRepository,
                                          PaymentRepository paymentRepository,
                                          ReceiptDocumentGenerator documentGenerator) {
        this.receiptRepository = receiptRepository;
        this.paymentRepository = paymentRepository;
        this.documentGenerator = documentGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentReceipt> findByPaymentId(String paymentId) {
        return receiptRepository.findByPaymentId(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> renderDocument(String receiptId) {
        return receiptRepository.findById(receiptId)
                .flatMap(receipt -> paymentRepository.findById(receipt.getPaymentId())
                        .map(payment -> documentGenerator.generate(payment, receipt)));
    }
}

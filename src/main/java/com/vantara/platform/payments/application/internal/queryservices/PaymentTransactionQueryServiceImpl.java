package com.vantara.platform.payments.application.internal.queryservices;

import com.vantara.platform.payments.application.queryservices.PaymentTransactionQueryService;
import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;
import com.vantara.platform.payments.domain.repositories.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default payment transaction query service.
 */
@Service
public class PaymentTransactionQueryServiceImpl implements PaymentTransactionQueryService {

    private final PaymentTransactionRepository transactionRepository;

    public PaymentTransactionQueryServiceImpl(PaymentTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransaction> findByPaymentId(String paymentId) {
        return transactionRepository.findByPaymentId(paymentId);
    }
}

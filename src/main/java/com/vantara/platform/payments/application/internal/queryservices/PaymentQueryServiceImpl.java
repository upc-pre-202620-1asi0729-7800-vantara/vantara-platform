package com.vantara.platform.payments.application.internal.queryservices;

import com.vantara.platform.payments.application.queryservices.PaymentQueryService;
import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default payment query service.
 */
@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAllOrderByCreatedAtAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findById(String id) {
        return paymentRepository.findById(id);
    }
}

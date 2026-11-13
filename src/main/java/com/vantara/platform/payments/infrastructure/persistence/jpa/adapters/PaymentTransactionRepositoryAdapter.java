package com.vantara.platform.payments.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;
import com.vantara.platform.payments.domain.repositories.PaymentTransactionRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.PaymentTransactionJpaRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.mappers.PaymentTransactionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA adapter for the payment transaction repository port.
 */
@Repository
public class PaymentTransactionRepositoryAdapter implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository jpaRepository;
    private final PaymentTransactionPersistenceMapper mapper;

    public PaymentTransactionRepositoryAdapter(PaymentTransactionJpaRepository jpaRepository,
                                               PaymentTransactionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public List<PaymentTransaction> findByPaymentId(String paymentId) {
        return jpaRepository.findByPaymentIdOrderByAttemptedAtAsc(paymentId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

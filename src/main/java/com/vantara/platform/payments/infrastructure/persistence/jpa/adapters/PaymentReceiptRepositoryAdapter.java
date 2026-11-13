package com.vantara.platform.payments.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import com.vantara.platform.payments.domain.repositories.PaymentReceiptRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.PaymentReceiptJpaRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.mappers.PaymentReceiptPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for the payment receipt repository port.
 */
@Repository
public class PaymentReceiptRepositoryAdapter implements PaymentReceiptRepository {

    private final PaymentReceiptJpaRepository jpaRepository;
    private final PaymentReceiptPersistenceMapper mapper;

    public PaymentReceiptRepositoryAdapter(PaymentReceiptJpaRepository jpaRepository,
                                           PaymentReceiptPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PaymentReceipt save(PaymentReceipt receipt) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(receipt)));
    }

    @Override
    public Optional<PaymentReceipt> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PaymentReceipt> findByPaymentId(String paymentId) {
        return jpaRepository.findByPaymentIdOrderByIssuedAtAsc(paymentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}

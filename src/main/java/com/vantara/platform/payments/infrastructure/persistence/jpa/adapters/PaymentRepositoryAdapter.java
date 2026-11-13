package com.vantara.platform.payments.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.repositories.PaymentRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.PaymentJpaRepository;
import com.vantara.platform.payments.infrastructure.persistence.jpa.mappers.PaymentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for the payment repository port.
 */
@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentPersistenceMapper mapper;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository, PaymentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAllOrderByCreatedAtAsc() {
        return jpaRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }
}

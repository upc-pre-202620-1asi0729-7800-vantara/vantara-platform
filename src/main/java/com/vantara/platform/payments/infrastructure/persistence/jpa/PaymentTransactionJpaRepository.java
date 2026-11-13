package com.vantara.platform.payments.infrastructure.persistence.jpa;

import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for payment transactions.
 */
public interface PaymentTransactionJpaRepository extends JpaRepository<PaymentTransactionJpaEntity, String> {

    /**
     * Returns the transactions of a payment ordered by attempt instant.
     *
     * @param paymentId payment identifier
     * @return matching transactions
     */
    List<PaymentTransactionJpaEntity> findByPaymentIdOrderByAttemptedAtAsc(String paymentId);
}

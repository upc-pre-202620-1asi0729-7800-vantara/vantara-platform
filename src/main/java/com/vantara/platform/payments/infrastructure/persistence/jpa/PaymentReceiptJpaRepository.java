package com.vantara.platform.payments.infrastructure.persistence.jpa;

import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentReceiptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for payment receipts.
 */
public interface PaymentReceiptJpaRepository extends JpaRepository<PaymentReceiptJpaEntity, String> {

    /**
     * Returns the receipts of a payment ordered by issue instant.
     *
     * @param paymentId payment identifier
     * @return matching receipts
     */
    List<PaymentReceiptJpaEntity> findByPaymentIdOrderByIssuedAtAsc(String paymentId);
}

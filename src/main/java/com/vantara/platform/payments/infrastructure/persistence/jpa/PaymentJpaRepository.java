package com.vantara.platform.payments.infrastructure.persistence.jpa;

import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for payments.
 */
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, String> {

    /**
     * Returns every payment ordered by creation instant ascending.
     *
     * @return ordered payments
     */
    List<PaymentJpaEntity> findAllByOrderByCreatedAtAsc();
}

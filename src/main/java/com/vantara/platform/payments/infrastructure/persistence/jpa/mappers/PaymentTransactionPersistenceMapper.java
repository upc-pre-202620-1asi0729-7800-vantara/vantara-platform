package com.vantara.platform.payments.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;
import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentTransactionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps payment transactions between the domain and JPA representations.
 */
@Component
public class PaymentTransactionPersistenceMapper {

    public PaymentTransactionJpaEntity toEntity(PaymentTransaction transaction) {
        return new PaymentTransactionJpaEntity(
                transaction.getId(),
                transaction.getPaymentId(),
                transaction.getProvider(),
                transaction.getProviderReference(),
                transaction.getStatus(),
                transaction.getAttemptedAt(),
                transaction.getFailureReason()
        );
    }

    public PaymentTransaction toDomain(PaymentTransactionJpaEntity entity) {
        return PaymentTransaction.rehydrate(
                entity.getId(),
                entity.getPaymentId(),
                entity.getProvider(),
                entity.getProviderReference(),
                entity.getStatus(),
                entity.getAttemptedAt(),
                entity.getFailureReason()
        );
    }
}

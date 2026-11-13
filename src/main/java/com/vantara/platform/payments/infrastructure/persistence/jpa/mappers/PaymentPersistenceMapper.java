package com.vantara.platform.payments.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps payments between the domain and JPA representations.
 */
@Component
public class PaymentPersistenceMapper {

    public PaymentJpaEntity toEntity(Payment payment) {
        var method = payment.getPaymentMethod();
        return new PaymentJpaEntity(
                payment.getId(),
                payment.getRancherId(),
                payment.getAppointmentId(),
                payment.getConcept(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                method == null ? null : method.brand(),
                method == null ? null : method.last4(),
                method == null ? null : method.holderName(),
                payment.getCreatedAt(),
                payment.getConfirmedAt(),
                payment.getCancelledAt()
        );
    }

    public Payment toDomain(PaymentJpaEntity entity) {
        return Payment.rehydrate(
                entity.getId(),
                entity.getRancherId(),
                entity.getAppointmentId(),
                entity.getConcept(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                toPaymentMethod(entity),
                entity.getCreatedAt(),
                entity.getConfirmedAt(),
                entity.getCancelledAt()
        );
    }

    private PaymentMethod toPaymentMethod(PaymentJpaEntity entity) {
        if (entity.getPaymentMethodBrand() == null || entity.getPaymentMethodLast4() == null) {
            return null;
        }
        return PaymentMethod.of(
                entity.getPaymentMethodBrand(),
                entity.getPaymentMethodLast4(),
                entity.getPaymentMethodHolder()
        );
    }
}

package com.vantara.platform.payments.infrastructure.persistence.jpa.mappers;

import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import com.vantara.platform.payments.infrastructure.persistence.jpa.entities.PaymentReceiptJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps payment receipts between the domain and JPA representations.
 */
@Component
public class PaymentReceiptPersistenceMapper {

    public PaymentReceiptJpaEntity toEntity(PaymentReceipt receipt) {
        return new PaymentReceiptJpaEntity(
                receipt.getId(),
                receipt.getPaymentId(),
                receipt.getReceiptNumber(),
                receipt.getIssuedAt(),
                receipt.getDocumentUrl()
        );
    }

    public PaymentReceipt toDomain(PaymentReceiptJpaEntity entity) {
        return PaymentReceipt.rehydrate(
                entity.getId(),
                entity.getPaymentId(),
                entity.getReceiptNumber(),
                entity.getIssuedAt(),
                entity.getDocumentUrl()
        );
    }
}

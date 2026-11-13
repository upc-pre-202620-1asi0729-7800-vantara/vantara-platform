package com.vantara.platform.payments.interfaces.rest.transform;

import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentReceiptResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps payment receipts to HTTP response resources.
 */
@Component
public class PaymentReceiptResourceMapper {

    public PaymentReceiptResource toResource(PaymentReceipt receipt) {
        return new PaymentReceiptResource(
                receipt.getId(),
                receipt.getPaymentId(),
                receipt.getReceiptNumber(),
                receipt.getIssuedAt(),
                receipt.getDocumentUrl()
        );
    }

    public List<PaymentReceiptResource> toResources(List<PaymentReceipt> receipts) {
        return receipts.stream().map(this::toResource).toList();
    }
}

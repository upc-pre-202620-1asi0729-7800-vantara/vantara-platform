package com.vantara.platform.payments.application.commandservices;

import com.vantara.platform.payments.application.commands.CreatePaymentCommand;
import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.shared.application.result.Result;

/**
 * Inbound port for payment commands.
 */
public interface PaymentCommandService {

    /**
     * Creates a payment and its simulated transaction and receipt.
     *
     * @param command creation command
     * @return created payment or a validation failure
     */
    Result<Payment, PaymentCommandFailure> handle(CreatePaymentCommand command);
}

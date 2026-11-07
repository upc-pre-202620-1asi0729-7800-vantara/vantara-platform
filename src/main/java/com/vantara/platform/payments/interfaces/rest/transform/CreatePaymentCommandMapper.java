package com.vantara.platform.payments.interfaces.rest.transform;

import com.vantara.platform.payments.application.commands.CreatePaymentCommand;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import com.vantara.platform.payments.interfaces.rest.resources.CreatePaymentResource;
import org.springframework.stereotype.Component;

/**
 * Maps the HTTP request resource to the create-payment command.
 */
@Component
public class CreatePaymentCommandMapper {

    public CreatePaymentCommand toCommand(CreatePaymentResource resource) {
        var methodResource = resource.paymentMethod();
        PaymentMethod paymentMethod = methodResource == null
                ? null
                : PaymentMethod.of(
                        methodResource.brand(),
                        methodResource.last4(),
                        methodResource.holderName()
                );
        return new CreatePaymentCommand(
                resource.rancherId(),
                resource.appointmentId(),
                resource.concept(),
                resource.amount(),
                resource.currency(),
                paymentMethod
        );
    }
}

package com.vantara.platform.payments.application.internal.commandservices;

import com.vantara.platform.payments.application.commandservices.PaymentCommandFailure;
import com.vantara.platform.payments.application.commandservices.PaymentCommandService;
import com.vantara.platform.payments.application.commands.CreatePaymentCommand;
import com.vantara.platform.payments.application.ports.PaymentGateway;
import com.vantara.platform.payments.application.ports.ReceiptNumberGenerator;
import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import com.vantara.platform.payments.domain.model.entities.PaymentTransaction;
import com.vantara.platform.payments.domain.model.valueobjects.TransactionStatus;
import com.vantara.platform.payments.domain.repositories.PaymentReceiptRepository;
import com.vantara.platform.payments.domain.repositories.PaymentRepository;
import com.vantara.platform.payments.domain.repositories.PaymentTransactionRepository;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

/**
 * Creates payments and immediately simulates their charge and receipt.
 */
@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentReceiptRepository receiptRepository;
    private final PaymentGateway paymentGateway;
    private final ReceiptNumberGenerator receiptNumberGenerator;
    private final Clock clock;

    public PaymentCommandServiceImpl(PaymentRepository paymentRepository,
                                     PaymentTransactionRepository transactionRepository,
                                     PaymentReceiptRepository receiptRepository,
                                     PaymentGateway paymentGateway,
                                     ReceiptNumberGenerator receiptNumberGenerator,
                                     Clock clock) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.receiptRepository = receiptRepository;
        this.paymentGateway = paymentGateway;
        this.receiptNumberGenerator = receiptNumberGenerator;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Result<Payment, PaymentCommandFailure> handle(CreatePaymentCommand command) {
        try {
            var now = clock.instant();
            var payment = Payment.create(
                    command.rancherId(),
                    command.appointmentId(),
                    command.concept(),
                    command.amount(),
                    command.currency(),
                    command.paymentMethod(),
                    now
            );
            var savedPayment = paymentRepository.save(payment);

            var gatewayResult = paymentGateway.charge(savedPayment, command.paymentMethod());
            var transactionStatus = gatewayResult.approved()
                    ? TransactionStatus.APPROVED
                    : TransactionStatus.DECLINED;
            var transaction = PaymentTransaction.create(
                    savedPayment.getId(),
                    gatewayResult.provider(),
                    gatewayResult.reference(),
                    transactionStatus,
                    gatewayResult.attemptedAt(),
                    gatewayResult.failureReason()
            );
            transactionRepository.save(transaction);

            if (gatewayResult.approved()) {
                var receiptId = UUID.randomUUID().toString();
                var documentUrl = "/api/v1/receipts/" + receiptId + "/document";
                var receipt = PaymentReceipt.create(
                        receiptId,
                        savedPayment.getId(),
                        receiptNumberGenerator.nextReceiptNumber(),
                        now,
                        documentUrl
                );
                receiptRepository.save(receipt);
            }

            return Result.success(savedPayment);
        } catch (IllegalArgumentException exception) {
            return Result.failure(new PaymentCommandFailure.Validation(exception.getMessage()));
        }
    }
}

package com.vantara.platform.payments;

import com.vantara.platform.payments.application.commands.CreatePaymentCommand;
import com.vantara.platform.payments.application.commandservices.PaymentCommandService;
import com.vantara.platform.payments.application.queryservices.PaymentQueryService;
import com.vantara.platform.payments.application.queryservices.PaymentReceiptQueryService;
import com.vantara.platform.payments.application.queryservices.PaymentTransactionQueryService;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentStatus;
import com.vantara.platform.payments.domain.model.valueobjects.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:vantara_payments_context;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class PaymentsContextIntegrationTest {

    @Autowired
    private PaymentCommandService commandService;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private PaymentTransactionQueryService transactionQueryService;

    @Autowired
    private PaymentReceiptQueryService receiptQueryService;

    @Test
    void createsPaymentWithTransactionAndPdfReceipt() {
        var result = commandService.handle(new CreatePaymentCommand(
                "usr-002",
                "app-001",
                "Control veterinario",
                new BigDecimal("150.00"),
                "PEN",
                PaymentMethod.of("VISA", "4582", "JUAN QUISPE")
        ));

        assertThat(result.isSuccess()).isTrue();
        var payment = result.toOptional().orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getAppointmentId()).isEqualTo("app-001");
        assertThat(paymentQueryService.findById(payment.getId())).isPresent();

        var transactions = transactionQueryService.findByPaymentId(payment.getId());
        assertThat(transactions).hasSize(1);
        assertThat(transactions.getFirst().getStatus()).isEqualTo(TransactionStatus.APPROVED);

        var receipts = receiptQueryService.findByPaymentId(payment.getId());
        assertThat(receipts).hasSize(1);
        assertThat(receipts.getFirst().getReceiptNumber()).startsWith("B001-");
        assertThat(receipts.getFirst().getDocumentUrl()).endsWith("/document");

        var pdf = receiptQueryService.renderDocument(receipts.getFirst().getId()).orElseThrow();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    void supportsSubscriptionPaymentWithoutAppointment() {
        var result = commandService.handle(new CreatePaymentCommand(
                "usr-002",
                null,
                "Suscripción Profesional",
                new BigDecimal("49.90"),
                "PEN",
                null
        ));

        assertThat(result.isSuccess()).isTrue();
        var payment = result.toOptional().orElseThrow();
        assertThat(payment.getAppointmentId()).isNull();
        assertThat(payment.getPaymentMethod()).isNull();
        assertThat(paymentQueryService.findAll())
                .extracting("id")
                .contains(payment.getId());
    }
}

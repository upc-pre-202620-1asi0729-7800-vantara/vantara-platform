package com.vantara.platform.payments.interfaces.rest;

import com.vantara.platform.payments.application.queryservices.PaymentTransactionQueryService;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentTransactionResource;
import com.vantara.platform.payments.interfaces.rest.transform.PaymentTransactionResourceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST entry point for payment transactions.
 */
@RestController
@RequestMapping(value = "/api/v1/transactions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payment transactions", description = "Payment transaction attempts")
public class PaymentTransactionsController {

    private final PaymentTransactionQueryService queryService;
    private final PaymentTransactionResourceMapper resourceMapper;
    private final MessageSource messageSource;

    public PaymentTransactionsController(PaymentTransactionQueryService queryService,
                                         PaymentTransactionResourceMapper resourceMapper,
                                         MessageSource messageSource) {
        this.queryService = queryService;
        this.resourceMapper = resourceMapper;
        this.messageSource = messageSource;
    }

    /**
     * Returns the transactions of a payment. An empty list is returned when the
     * payment has no transactions.
     *
     * @param paymentId payment identifier
     * @param locale    request locale
     * @return transactions or a bad-request problem
     */
    @Operation(summary = "List transactions by payment",
            description = "Returns an empty list when the payment has no transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found"),
            @ApiResponse(responseCode = "400", description = "Missing paymentId",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<?> findByPaymentId(
            @RequestParam(name = "paymentId", required = false) String paymentId,
            Locale locale) {
        if (paymentId == null || paymentId.isBlank()) {
            return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("payment.error.transaction.paymentId.required", null, locale)));
        }
        List<PaymentTransactionResource> transactions =
                resourceMapper.toResources(queryService.findByPaymentId(paymentId));
        return ResponseEntity.ok(transactions);
    }
}

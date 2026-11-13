package com.vantara.platform.payments.interfaces.rest;

import com.vantara.platform.payments.application.queryservices.PaymentReceiptQueryService;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentReceiptResource;
import com.vantara.platform.payments.interfaces.rest.transform.PaymentReceiptResourceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST entry point for payment receipts and their PDF documents.
 */
@RestController
@RequestMapping("/api/v1/receipts")
@Tag(name = "Payment receipts", description = "Payment receipts and PDF documents")
public class PaymentReceiptsController {

    private final PaymentReceiptQueryService queryService;
    private final PaymentReceiptResourceMapper resourceMapper;
    private final MessageSource messageSource;

    public PaymentReceiptsController(PaymentReceiptQueryService queryService,
                                     PaymentReceiptResourceMapper resourceMapper,
                                     MessageSource messageSource) {
        this.queryService = queryService;
        this.resourceMapper = resourceMapper;
        this.messageSource = messageSource;
    }

    /**
     * Returns the receipts of a payment. An empty list is returned when the
     * payment has no receipts.
     *
     * @param paymentId payment identifier
     * @param locale    request locale
     * @return receipts or a bad-request problem
     */
    @Operation(summary = "List receipts by payment",
            description = "Returns an empty list when the payment has no receipts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipts found"),
            @ApiResponse(responseCode = "400", description = "Missing paymentId",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPaymentId(
            @RequestParam(name = "paymentId", required = false) String paymentId,
            Locale locale) {
        if (paymentId == null || paymentId.isBlank()) {
            return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("payment.error.receipt.paymentId.required", null, locale)));
        }
        List<PaymentReceiptResource> receipts =
                resourceMapper.toResources(queryService.findByPaymentId(paymentId));
        return ResponseEntity.ok(receipts);
    }

    /**
     * Downloads the PDF document of a receipt.
     *
     * @param id     receipt identifier
     * @param locale request locale
     * @return PDF bytes or a not-found problem
     */
    @Operation(summary = "Download a receipt PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF document",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "Receipt not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/document")
    public ResponseEntity<?> downloadDocument(@PathVariable String id, Locale locale) {
        return queryService.renderDocument(id)
                .<ResponseEntity<?>>map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"receipt-" + id + ".pdf\"")
                        .body(bytes))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                messageSource.getMessage("payment.error.receipt.notFound", new Object[]{id}, locale))));
    }
}

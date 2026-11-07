package com.vantara.platform.payments.interfaces.rest;

import com.vantara.platform.payments.application.commandservices.PaymentCommandService;
import com.vantara.platform.payments.application.queryservices.PaymentQueryService;
import com.vantara.platform.payments.interfaces.rest.resources.CreatePaymentResource;
import com.vantara.platform.payments.interfaces.rest.resources.PaymentResource;
import com.vantara.platform.payments.interfaces.rest.transform.CreatePaymentCommandMapper;
import com.vantara.platform.payments.interfaces.rest.transform.PaymentResourceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST entry point for payment creation and payment history.
 */
@RestController
@RequestMapping(value = "/api/v1/payments", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Payment checkout and history")
public class PaymentsController {

    private final PaymentCommandService commandService;
    private final PaymentQueryService queryService;
    private final CreatePaymentCommandMapper commandMapper;
    private final PaymentResourceMapper resourceMapper;
    private final MessageSource messageSource;

    public PaymentsController(PaymentCommandService commandService,
                              PaymentQueryService queryService,
                              CreatePaymentCommandMapper commandMapper,
                              PaymentResourceMapper resourceMapper,
                              MessageSource messageSource) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.commandMapper = commandMapper;
        this.resourceMapper = resourceMapper;
        this.messageSource = messageSource;
    }

    /**
     * Creates a payment, its simulated transaction and its receipt.
     *
     * @param resource request payload
     * @param locale   request locale
     * @return created payment or a validation problem
     */
    @Operation(summary = "Create a payment", description = "Creates a simulated paid payment and its receipt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = PaymentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreatePaymentResource resource, Locale locale) {
        var result = commandService.handle(commandMapper.toCommand(resource));
        if (result.isFailure()) {
            var failure = result.failure().orElseThrow();
            var message = messageSource.getMessage(failure.messageKey(), null, failure.messageKey(), locale);
            return ResponseEntity.badRequest()
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message));
        }

        var payment = result.success().orElseThrow();
        return ResponseEntity.created(URI.create("/api/v1/payments/" + payment.getId()))
                .body(resourceMapper.toResource(payment));
    }

    /**
     * Returns all payments ordered by creation instant ascending.
     *
     * @return ordered payments
     */
    @Operation(summary = "List payments", description = "Returns payments ordered by creation instant ascending")
    @ApiResponse(responseCode = "200", description = "Payments found")
    @GetMapping
    public ResponseEntity<List<PaymentResource>> findAll() {
        return ResponseEntity.ok(resourceMapper.toResources(queryService.findAll()));
    }

    /**
     * Returns a payment by identifier.
     *
     * @param id     payment identifier
     * @param locale request locale
     * @return payment or a not-found problem
     */
    @Operation(summary = "Get a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResource.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id, Locale locale) {
        return queryService.findById(id)
                .<ResponseEntity<?>>map(payment -> ResponseEntity.ok(resourceMapper.toResource(payment)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                messageSource.getMessage("payment.error.notFound", new Object[]{id}, locale))));
    }
}

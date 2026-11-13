package com.vantara.platform.iam.interfaces.rest;

import com.vantara.platform.iam.application.commandservices.ContactRequestCommandService;
import com.vantara.platform.iam.interfaces.rest.resources.ContactRequestResource;
import com.vantara.platform.iam.interfaces.rest.transform.SendContactRequestCommandFromResourceAssembler;
import com.vantara.platform.shared.interfaces.rest.resources.MessageResource;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoint of the contact form.
 */
@RestController
@RequestMapping(value = "/api/v1/contact-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Contact", description = "Contact form")
public class ContactRequestsController {

    private final ContactRequestCommandService contactRequestCommandService;
    private final MessageSource messageSource;

    public ContactRequestsController(
            ContactRequestCommandService contactRequestCommandService, MessageSource messageSource) {
        this.contactRequestCommandService = contactRequestCommandService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @Operation(summary = "Send the contact form", description = "Stores the request and emails a confirmation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Request received"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)})
    public ResponseEntity<?> sendContactRequest(@Valid @RequestBody ContactRequestResource resource) {
        var result = contactRequestCommandService.handle(
                SendContactRequestCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                request -> new MessageResource(messageSource.getMessage(
                        "message.contact-request.received", null, "message.contact-request.received",
                        LocaleContextHolder.getLocale())),
                HttpStatus.CREATED);
    }
}

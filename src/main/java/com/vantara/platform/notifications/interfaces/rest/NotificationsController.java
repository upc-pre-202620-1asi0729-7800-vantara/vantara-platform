package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.notifications.application.commands.*;
import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.application.queries.*;
import com.vantara.platform.notifications.application.queryservices.NotificationQueryService;
import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.interfaces.rest.resources.*;
import com.vantara.platform.notifications.interfaces.rest.transform.NotificationResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {
    private final NotificationCommandService commands;
    private final NotificationQueryService queries;
    private final AuthenticatedNotificationRecipient recipients;

    public NotificationsController(NotificationCommandService commands, NotificationQueryService queries,
                                   AuthenticatedNotificationRecipient recipients) {
        this.commands = commands;
        this.queries = queries;
        this.recipients = recipients;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> create(Principal principal,
                                                        @Valid @RequestBody CreateNotificationResource request) {
        var recipient = recipients.resolve(principal);
        if (request.recipientUserId() != null && !request.recipientUserId().equals(Long.toString(recipient.value()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        var notification = commands.handle(new CreateNotificationCommand(recipient, request.type(),
                new NotificationContent(request.title(), request.description()),
                new RelatedEntity(request.relatedEntityType(), request.relatedEntityId())));
        var location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(notification.id()).toUri();
        return ResponseEntity.created(location).body(NotificationResourceAssembler.fromDomain(notification));
    }

    @GetMapping
    public List<NotificationResource> list(Principal principal,
                                           @RequestParam(required = false) NotificationType type,
                                           @RequestParam(required = false) String status) {
        var recipient = recipients.resolve(principal);
        var filter = parseStatus(status);
        return queries.handle(new FindNotificationsQuery(recipient, type, filter)).stream()
                .map(NotificationResourceAssembler::fromDomain).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResource> find(Principal principal, @PathVariable UUID id) {
        return ResponseEntity.of(queries.handle(new FindNotificationByIdQuery(id, recipients.resolve(principal)))
                .map(NotificationResourceAssembler::fromDomain));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NotificationResource> markAsRead(Principal principal, @PathVariable UUID id) {
        return ResponseEntity.of(commands.handle(new MarkNotificationAsReadCommand(id, recipients.resolve(principal)))
                .map(NotificationResourceAssembler::fromDomain));
    }

    private NotificationStatus parseStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return NotificationStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException invalidStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification status");
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ProblemDetail> handleInvalidParameter(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid " + exception.getName()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ProblemDetail> handleStatus(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getBody());
    }

    @ExceptionHandler(NotificationNotSentException.class)
    ResponseEntity<ProblemDetail> handleNotSent(NotificationNotSentException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage()));
    }
}

package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.notifications.application.commands.*;
import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.application.queries.*;
import com.vantara.platform.notifications.application.queryservices.NotificationQueryService;
import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.interfaces.rest.resources.*;
import com.vantara.platform.notifications.interfaces.rest.transform.NotificationResourceAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Notifications of the authenticated user. The recipient is always resolved from the bearer token,
 * so a user can only read, create and mark their own notifications.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {
    private final NotificationCommandService commands;
    private final NotificationQueryService queries;
    private final IamContextFacade iamContextFacade;

    public NotificationsController(NotificationCommandService commands, NotificationQueryService queries,
                                   IamContextFacade iamContextFacade) {
        this.commands = commands;
        this.queries = queries;
        this.iamContextFacade = iamContextFacade;
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication authentication,
                                    @Valid @RequestBody CreateNotificationResource request) {
        var recipient = resolveRecipient(authentication);
        if (recipient.isEmpty()) {
            return unauthorized();
        }
        var notification = commands.handle(new CreateNotificationCommand(recipient.get(), request.type(),
                new NotificationContent(request.title(), request.description()),
                new RelatedEntity(request.relatedEntityType(), request.relatedEntityId())));
        var location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(notification.id()).toUri();
        return ResponseEntity.created(location).body(NotificationResourceAssembler.fromDomain(notification));
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication authentication,
                                  @RequestParam(required = false) NotificationType type,
                                  @RequestParam(required = false) String status) {
        var recipient = resolveRecipient(authentication);
        if (recipient.isEmpty()) {
            return unauthorized();
        }
        var filter = parseStatus(status);
        return ResponseEntity.ok(queries.handle(new FindNotificationsQuery(recipient.get(), type, filter)).stream()
                .map(NotificationResourceAssembler::fromDomain).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(Authentication authentication, @PathVariable UUID id) {
        var recipient = resolveRecipient(authentication);
        if (recipient.isEmpty()) {
            return unauthorized();
        }
        return ResponseEntity.of(queries.handle(new FindNotificationByIdQuery(id, recipient.get()))
                .map(NotificationResourceAssembler::fromDomain));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> markAsRead(Authentication authentication, @PathVariable UUID id) {
        var recipient = resolveRecipient(authentication);
        if (recipient.isEmpty()) {
            return unauthorized();
        }
        return ResponseEntity.of(commands.handle(new MarkNotificationAsReadCommand(id, recipient.get()))
                .map(NotificationResourceAssembler::fromDomain));
    }

    private Optional<RecipientId> resolveRecipient(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return Optional.empty();
        }
        return iamContextFacade.fetchUserIdByEmail(authentication.getName()).map(RecipientId::new);
    }

    private static ResponseEntity<?> unauthorized() {
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                new ApplicationError("UNAUTHORIZED", "Authentication is required"));
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

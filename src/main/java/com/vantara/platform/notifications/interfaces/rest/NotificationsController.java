package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.notifications.application.commandservices.NotificationCommandService;
import com.vantara.platform.notifications.application.queryservices.NotificationQueryService;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.vantara.platform.notifications.domain.model.queries.GetNotificationsByRecipientQuery;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.vantara.platform.notifications.interfaces.rest.resources.NotificationResource;
import com.vantara.platform.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.vantara.platform.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Notifications of the authenticated user. The recipient is always resolved from the bearer token,
 * so a user can only read, create and mark their own notifications.
 */
@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "In-app notifications of the authenticated user")
@SecurityRequirement(name = "bearerAuth")
public class NotificationsController {
    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;
    private final IamContextFacade iamContextFacade;

    public NotificationsController(NotificationCommandService notificationCommandService,
                                   NotificationQueryService notificationQueryService,
                                   IamContextFacade iamContextFacade) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    @PostMapping
    @Operation(summary = "Create a notification for the authenticated user",
            description = "The notification is sent right away. The recipient is the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)})
    public ResponseEntity<?> createNotification(Authentication authentication,
                                                @Valid @RequestBody CreateNotificationResource resource) {
        var recipientId = resolveRecipientId(authentication);
        if (recipientId.isEmpty()) {
            return unauthorized();
        }
        var result = notificationCommandService.handle(
                CreateNotificationCommandFromResourceAssembler.toCommandFromResource(recipientId.get(), resource));
        return switch (result) {
            case Result.Success<Notification, ApplicationError> success -> {
                var location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                        .buildAndExpand(success.value().getId().value()).toUri();
                yield ResponseEntity.created(location)
                        .body(NotificationResourceFromEntityAssembler.toResourceFromEntity(success.value()));
            }
            case Result.Failure<Notification, ApplicationError> failure ->
                    ErrorResponseAssembler.toErrorResponseFromApplicationError(failure.error());
        };
    }

    @GetMapping
    @Operation(summary = "List the notifications of the authenticated user", description = "Newest first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResource.class)))),
            @ApiResponse(responseCode = "400", description = "Unknown type or status", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)})
    public ResponseEntity<?> getNotifications(
            Authentication authentication,
            @Parameter(description = "Only notifications of this type") @RequestParam(required = false) NotificationType type,
            @Parameter(description = "Only notifications with this status: scheduled, unread or read (any case)",
                    example = "unread") @RequestParam(required = false) String status) {
        var recipientId = resolveRecipientId(authentication);
        if (recipientId.isEmpty()) {
            return unauthorized();
        }
        NotificationStatus statusFilter = null;
        if (status != null) {
            try {
                statusFilter = NotificationStatus.valueOf(status.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                        ApplicationError.validationError("status", "Unknown notification status: " + status));
            }
        }
        var notifications = notificationQueryService.handle(
                new GetNotificationsByRecipientQuery(recipientId.get(), type, statusFilter));
        return ResponseEntity.ok(notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get a notification of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "400", description = "The id is not a UUID", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "The user has no such notification", content = @Content)})
    public ResponseEntity<?> getNotificationById(
            Authentication authentication,
            @Parameter(description = "Notification id", example = "3f2b8c1e-6a4d-4c0e-9d57-2a1f0b7e5c11")
            @PathVariable UUID notificationId) {
        var recipientId = resolveRecipientId(authentication);
        if (recipientId.isEmpty()) {
            return unauthorized();
        }
        return notificationQueryService.handle(new GetNotificationByIdQuery(notificationId, recipientId.get()))
                .<ResponseEntity<?>>map(notification -> ResponseEntity.ok(
                        NotificationResourceFromEntityAssembler.toResourceFromEntity(notification)))
                .orElseGet(() -> ErrorResponseAssembler.toErrorResponseFromApplicationError(
                        ApplicationError.notFound("notification", notificationId.toString())));
    }

    @PatchMapping("/{notificationId}")
    @Operation(summary = "Mark a notification as read",
            description = "Records the first read time; reading it again keeps that time.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "400", description = "The id is not a UUID", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "The user has no such notification", content = @Content),
            @ApiResponse(responseCode = "422", description = "The notification is still scheduled", content = @Content)})
    public ResponseEntity<?> markNotificationAsRead(
            Authentication authentication,
            @Parameter(description = "Notification id", example = "3f2b8c1e-6a4d-4c0e-9d57-2a1f0b7e5c11")
            @PathVariable UUID notificationId) {
        var recipientId = resolveRecipientId(authentication);
        if (recipientId.isEmpty()) {
            return unauthorized();
        }
        return ResponseEntityAssembler.toResponseEntityFromResult(
                notificationCommandService.handle(new MarkNotificationAsReadCommand(notificationId, recipientId.get())),
                NotificationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    private Optional<Long> resolveRecipientId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return Optional.empty();
        }
        return iamContextFacade.fetchUserIdByEmail(authentication.getName());
    }

    private static ResponseEntity<?> unauthorized() {
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                new ApplicationError("UNAUTHORIZED", "Authentication is required"));
    }
}

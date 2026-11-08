package com.vantara.platform.iam.interfaces.rest;

import com.vantara.platform.iam.application.queryservices.UserQueryService;
import com.vantara.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.vantara.platform.iam.interfaces.rest.resources.UserResource;
import com.vantara.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account data of the authenticated user. There is no administrator role, so listing every user is not
 * exposed: a user can only read their own account.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Account of the authenticated user")
@SecurityRequirement(name = "bearerAuth")
public class UsersController {

    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)})
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return userQueryService.handle(new GetUserByEmailQuery(authentication.getName()))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElseGet(() -> notFound(authentication.getName()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get an account by id",
            description = "Only the authenticated user's own id is accepted; any other id answers 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)})
    public ResponseEntity<?> getUserById(@PathVariable Long userId, Authentication authentication) {
        return userQueryService.handle(new GetUserByEmailQuery(authentication.getName()))
                .filter(user -> user.getId().equals(userId))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElseGet(() -> notFound(String.valueOf(userId)));
    }

    private static ResponseEntity<?> notFound(String identifier) {
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.notFound("user", identifier));
    }
}

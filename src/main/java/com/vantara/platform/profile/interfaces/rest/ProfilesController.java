package com.vantara.platform.profile.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.profile.application.commandservices.ProfileCommandService;
import com.vantara.platform.profile.application.queryservices.ProfileQueryService;
import com.vantara.platform.profile.domain.model.commands.CreateOrUpdateProfileCommand;
import com.vantara.platform.profile.domain.model.queries.GetProfileByUserIdQuery;
import com.vantara.platform.profile.interfaces.rest.resources.ProfileResource;
import com.vantara.platform.profile.interfaces.rest.resources.UpdateProfileResource;
import com.vantara.platform.profile.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.vantara.platform.profile.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Personal data of the authenticated user. The account identity is resolved from the
 * bearer token, so a user can only read and edit their own profile.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Personal data and preferences of the authenticated user")
@SecurityRequirement(name = "bearerAuth")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final IamContextFacade iamContextFacade;

    public ProfilesController(ProfileCommandService profileCommandService,
                              ProfileQueryService profileQueryService,
                              IamContextFacade iamContextFacade) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the profile of the authenticated user",
            description = "Answers 404 until the user saves their profile for the first time.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "The profile does not exist yet", content = @Content)})
    public ResponseEntity<?> getCurrentProfile(Authentication authentication) {
        var userId = resolveUserId(authentication);
        if (userId.isEmpty()) {
            return accountNotFound(authentication);
        }
        return profileQueryService.handle(new GetProfileByUserIdQuery(userId.get()))
                .<ResponseEntity<?>>map(profile -> ResponseEntity.ok(
                        ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ErrorResponseAssembler.toErrorResponseFromApplicationError(
                        ApplicationError.notFound("profile", String.valueOf(userId.get()))));
    }

    @PutMapping("/me")
    @Operation(summary = "Create or update the profile of the authenticated user",
            description = "Creates the profile the first time it is saved, and replaces it afterwards.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)})
    public ResponseEntity<?> updateCurrentProfile(@RequestBody UpdateProfileResource resource,
                                                  Authentication authentication) {
        var userId = resolveUserId(authentication);
        if (userId.isEmpty()) {
            return accountNotFound(authentication);
        }
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId.get(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                profileCommandService.handle(command),
                ProfileResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    private Optional<Long> resolveUserId(Authentication authentication) {
        if (authentication == null) {
            return Optional.empty();
        }
        return iamContextFacade.fetchUserIdByEmail(authentication.getName());
    }

    private static ResponseEntity<?> accountNotFound(Authentication authentication) {
        var identifier = authentication == null ? "" : authentication.getName();
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                ApplicationError.notFound("user", identifier));
    }
}

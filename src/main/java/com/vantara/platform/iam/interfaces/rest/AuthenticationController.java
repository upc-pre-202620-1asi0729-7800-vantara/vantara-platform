package com.vantara.platform.iam.interfaces.rest;

import com.vantara.platform.iam.application.commandservices.UserCommandService;
import com.vantara.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.vantara.platform.iam.interfaces.rest.resources.RecoverPasswordResource;
import com.vantara.platform.iam.interfaces.rest.resources.RequestPasswordRecoveryResource;
import com.vantara.platform.iam.interfaces.rest.resources.SendVerificationCodeResource;
import com.vantara.platform.iam.interfaces.rest.resources.SignInResource;
import com.vantara.platform.iam.interfaces.rest.resources.SignUpResource;
import com.vantara.platform.iam.interfaces.rest.resources.UserResource;
import com.vantara.platform.iam.interfaces.rest.resources.VerifyEmailResource;
import com.vantara.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.RecoverPasswordCommandFromResourceAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.RequestPasswordRecoveryCommandFromResourceAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.SendVerificationCodeCommandFromResourceAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.vantara.platform.iam.interfaces.rest.transform.VerifyEmailCommandFromResourceAssembler;
import com.vantara.platform.shared.interfaces.rest.resources.MessageResource;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * Public endpoints to register, verify, sign in and recover an account.
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Sign-up, email verification, sign-in and password recovery")
public class AuthenticationController {

    private final UserCommandService userCommandService;
    private final MessageSource messageSource;

    public AuthenticationController(UserCommandService userCommandService, MessageSource messageSource) {
        this.userCommandService = userCommandService;
        this.messageSource = messageSource;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Register an account",
            description = "Creates an account pending verification and emails a six-digit code.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)})
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource resource) {
        var result = userCommandService.handle(SignUpCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, UserResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in", description = "Returns a bearer token for a verified account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content),
            @ApiResponse(responseCode = "422", description = "Email not verified", content = @Content)})
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        var result = userCommandService.handle(SignInCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify an email address", description = "Activates the account with the emailed code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Wrong code", content = @Content),
            @ApiResponse(responseCode = "422", description = "Code expired, used or attempts exceeded", content = @Content)})
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailResource resource) {
        var result = userCommandService.handle(VerifyEmailCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, UserResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PostMapping("/send-verification-code")
    @Operation(summary = "Resend the verification code",
            description = "Emails a new code to a pending account. One code per minute at most.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Request accepted"),
            @ApiResponse(responseCode = "422", description = "Account already verified or too many requests", content = @Content)})
    public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody SendVerificationCodeResource resource) {
        var result = userCommandService.handle(
                SendVerificationCodeCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ack -> message("message.verification-code.accepted"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/request-password-recovery")
    @Operation(summary = "Request a password recovery",
            description = "Always answers 202, whether the email is registered or not.")
    @ApiResponses({@ApiResponse(responseCode = "202", description = "Request accepted")})
    public ResponseEntity<?> requestPasswordRecovery(@Valid @RequestBody RequestPasswordRecoveryResource resource) {
        var result = userCommandService.handle(
                RequestPasswordRecoveryCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ack -> message("message.password-recovery.accepted"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/recover-password")
    @Operation(summary = "Set a new password", description = "Replaces the password using the emailed recovery code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password replaced"),
            @ApiResponse(responseCode = "400", description = "Wrong code or weak password", content = @Content),
            @ApiResponse(responseCode = "422", description = "Code expired, used or attempts exceeded", content = @Content)})
    public ResponseEntity<?> recoverPassword(@Valid @RequestBody RecoverPasswordResource resource) {
        var result = userCommandService.handle(
                RecoverPasswordCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, user -> message("message.password-recovery.done"), HttpStatus.OK);
    }

    private MessageResource message(String key) {
        return new MessageResource(messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale()));
    }
}

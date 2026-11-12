package com.vantara.platform.iam.application.internal.commandservices;

import com.vantara.platform.iam.application.commandservices.Acknowledgement;
import com.vantara.platform.iam.application.internal.outboundservices.codes.VerificationCodeGenerator;
import com.vantara.platform.iam.application.internal.outboundservices.email.EmailService;
import com.vantara.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.vantara.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.aggregates.VerificationCode;
import com.vantara.platform.iam.domain.model.commands.RecoverPasswordCommand;
import com.vantara.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vantara.platform.iam.domain.model.commands.SendVerificationCodeCommand;
import com.vantara.platform.iam.domain.model.commands.SignInCommand;
import com.vantara.platform.iam.domain.model.commands.SignUpCommand;
import com.vantara.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.valueobjects.AccountStatus;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.iam.domain.repositories.VerificationCodeRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserCommandServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Role RANCHER = Role.reconstitute(1L, Roles.ROLE_RANCHER);
    private static final EmailAddress EMAIL = new EmailAddress("rancher@example.com");

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    VerificationCodeRepository verificationCodeRepository;
    @Mock
    HashingService hashingService;
    @Mock
    TokenService tokenService;
    @Mock
    EmailService emailService;
    @Mock
    VerificationCodeGenerator codeGenerator;

    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        when(hashingService.encode(any())).thenReturn("hashed");
        when(hashingService.matches("Password1", "hashed")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user.getId() != null ? user : User.reconstitute(
                    7L, user.getEmail(), user.getPasswordHash(), user.getRoles(), user.getStatus(), user.getLastSignInAt());
        });
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenAnswer(i -> i.getArgument(0));
        when(codeGenerator.generate()).thenReturn(new VerificationCodeValue("123456"));
        when(roleRepository.findByName(Roles.ROLE_RANCHER)).thenReturn(Optional.of(RANCHER));
        when(tokenService.generateToken(any())).thenReturn("jwt-token");
        service = new UserCommandServiceImpl(userRepository, roleRepository, verificationCodeRepository,
                hashingService, tokenService, emailService, codeGenerator, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private User user(AccountStatus status) {
        return User.reconstitute(7L, EMAIL, new PasswordHash("hashed"), List.of(RANCHER), status, null);
    }

    private VerificationCode code(VerificationPurpose purpose, Instant issuedAt) {
        return VerificationCode.reconstitute(3L, new UserId(7L), new VerificationCodeValue("123456"), purpose,
                issuedAt, issuedAt.plus(Duration.ofMinutes(15)), null, 0);
    }

    private static ApplicationError errorOf(Result<?, ApplicationError> result) {
        assertTrue(result.isFailure(), "a failure was expected");
        return ((Result.Failure<?, ApplicationError>) result).error();
    }

    private static <T> T valueOf(Result<T, ApplicationError> result) {
        assertTrue(result.isSuccess(), "a success was expected");
        return ((Result.Success<T, ApplicationError>) result).value();
    }

    // ---- sign up

    @Test
    void signUpRegistersUserIssuesCodeAndSendsEmail() {
        var result = service.handle(new SignUpCommand("Rancher@Example.com", "Password1", null));

        var created = valueOf(result);
        assertEquals(AccountStatus.PENDING_VERIFICATION, created.getStatus());
        assertEquals(EMAIL, created.getEmail());
        assertEquals("hashed", created.getPasswordHash().value());
        verify(verificationCodeRepository).save(any(VerificationCode.class));
        verify(emailService).sendEmailVerificationCode(
                eq(EMAIL), eq(new VerificationCodeValue("123456")), eq(NOW.plus(Duration.ofMinutes(15))));
    }

    @Test
    void signUpFailsWhenEmailIsAlreadyRegistered() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        var error = errorOf(service.handle(new SignUpCommand("rancher@example.com", "Password1", null)));

        assertEquals("USER_CONFLICT", error.code());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUpFailsWithWeakPassword() {
        var error = errorOf(service.handle(new SignUpCommand("rancher@example.com", "short", null)));

        assertEquals("VALIDATION_ERROR", error.code());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUpFailsWithInvalidEmailOrUnknownRole() {
        assertEquals("VALIDATION_ERROR",
                errorOf(service.handle(new SignUpCommand("not-an-email", "Password1", null))).code());
        assertEquals("VALIDATION_ERROR",
                errorOf(service.handle(new SignUpCommand("rancher@example.com", "Password1", "ROLE_ADMIN"))).code());
    }

    @Test
    void signUpFailsWhenTheRoleIsNotSeeded() {
        when(roleRepository.findByName(Roles.ROLE_VETERINARIAN)).thenReturn(Optional.empty());

        var error = errorOf(service.handle(new SignUpCommand("rancher@example.com", "Password1", "ROLE_VETERINARIAN")));

        assertEquals("ROLE_NOT_FOUND", error.code());
    }

    // ---- sign in

    @Test
    void signInReturnsUserAndTokenAndRecordsTheSignIn() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.ACTIVE)));

        var authenticated = valueOf(service.handle(new SignInCommand("rancher@example.com", "Password1")));

        assertEquals("jwt-token", authenticated.token());
        assertEquals(NOW, authenticated.user().getLastSignInAt());
        verify(tokenService).generateToken("rancher@example.com");
    }

    @Test
    void signInAnswersTheSameErrorForUnknownEmailAndWrongPassword() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.ACTIVE)));
        when(userRepository.findByEmail(new EmailAddress("ghost@example.com"))).thenReturn(Optional.empty());

        var wrongPassword = errorOf(service.handle(new SignInCommand("rancher@example.com", "Wrong1234")));
        var unknownEmail = errorOf(service.handle(new SignInCommand("ghost@example.com", "Password1")));

        assertEquals(wrongPassword, unknownEmail);
        assertEquals("INVALID_CREDENTIALS", wrongPassword.code());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void signInFailsWhenEmailIsNotVerified() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));

        var error = errorOf(service.handle(new SignInCommand("rancher@example.com", "Password1")));

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
    }

    // ---- verify email

    @Test
    void verifyEmailActivatesTheAccountAndConsumesTheCode() {
        var stored = code(VerificationPurpose.EMAIL_VERIFICATION, NOW.minusSeconds(60));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(7L), VerificationPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(stored));

        var verified = valueOf(service.handle(new VerifyEmailCommand("rancher@example.com", "123456")));

        assertEquals(AccountStatus.ACTIVE, verified.getStatus());
        assertTrue(stored.isConsumed());
        verify(verificationCodeRepository).save(stored);
    }

    @Test
    void verifyEmailSavesTheFailedAttemptWhenTheCodeIsWrong() {
        var stored = code(VerificationPurpose.EMAIL_VERIFICATION, NOW.minusSeconds(60));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(7L), VerificationPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(stored));

        var error = errorOf(service.handle(new VerifyEmailCommand("rancher@example.com", "000000")));

        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals(1, stored.getFailedAttempts());
        verify(verificationCodeRepository).save(stored);
        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyEmailFailsWhenTheCodeExpired() {
        var stored = code(VerificationPurpose.EMAIL_VERIFICATION, NOW.minus(Duration.ofHours(1)));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(7L), VerificationPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(stored));

        var error = errorOf(service.handle(new VerifyEmailCommand("rancher@example.com", "123456")));

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
    }

    @Test
    void verifyEmailFailsAfterTooManyAttempts() {
        var exhausted = VerificationCode.reconstitute(3L, new UserId(7L), new VerificationCodeValue("123456"),
                VerificationPurpose.EMAIL_VERIFICATION, NOW.minusSeconds(60), NOW.plusSeconds(600), null,
                VerificationCode.MAX_FAILED_ATTEMPTS);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(7L), VerificationPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(exhausted));

        var error = errorOf(service.handle(new VerifyEmailCommand("rancher@example.com", "123456")));

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        assertTrue(error.details().contains("Too many"));
    }

    @Test
    void verifyEmailForUnknownAccountLooksLikeAWrongCode() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        var error = errorOf(service.handle(new VerifyEmailCommand("rancher@example.com", "123456")));

        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals("The verification code is incorrect", error.details());
    }

    // ---- resend

    @Test
    void sendVerificationCodeIssuesAnotherCodeForAPendingAccount() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(any(), any()))
                .thenReturn(Optional.of(code(VerificationPurpose.EMAIL_VERIFICATION, NOW.minusSeconds(120))));

        var result = service.handle(new SendVerificationCodeCommand("rancher@example.com"));

        assertEquals(Acknowledgement.ACCEPTED, valueOf(result));
        verify(emailService).sendEmailVerificationCode(eq(EMAIL), any(), any());
    }

    @Test
    void sendVerificationCodeIsRateLimited() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.PENDING_VERIFICATION)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(any(), any()))
                .thenReturn(Optional.of(code(VerificationPurpose.EMAIL_VERIFICATION, NOW.minusSeconds(10))));

        var error = errorOf(service.handle(new SendVerificationCodeCommand("rancher@example.com")));

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        verify(emailService, never()).sendEmailVerificationCode(any(), any(), any());
    }

    @Test
    void sendVerificationCodeForUnknownAccountSendsNothingButSucceeds() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        var result = service.handle(new SendVerificationCodeCommand("rancher@example.com"));

        assertEquals(Acknowledgement.ACCEPTED, valueOf(result));
        verify(emailService, never()).sendEmailVerificationCode(any(), any(), any());
    }

    // ---- password recovery

    @Test
    void requestPasswordRecoverySucceedsWithoutRevealingUnknownEmails() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        var result = service.handle(new RequestPasswordRecoveryCommand("rancher@example.com"));

        assertEquals(Acknowledgement.ACCEPTED, valueOf(result));
        verify(emailService, never()).sendPasswordRecoveryCode(any(), any(), any());
    }

    @Test
    void requestPasswordRecoverySendsACodeToAKnownAccount() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.ACTIVE)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(any(), any())).thenReturn(Optional.empty());

        var result = service.handle(new RequestPasswordRecoveryCommand("rancher@example.com"));

        assertEquals(Acknowledgement.ACCEPTED, valueOf(result));
        verify(emailService).sendPasswordRecoveryCode(eq(EMAIL), eq(new VerificationCodeValue("123456")), any());
    }

    @Test
    void recoverPasswordReplacesTheHash() {
        var stored = code(VerificationPurpose.PASSWORD_RECOVERY, NOW.minusSeconds(60));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.ACTIVE)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(7L), VerificationPurpose.PASSWORD_RECOVERY))
                .thenReturn(Optional.of(stored));
        when(hashingService.encode("NewPassword1")).thenReturn("new-hash");

        var recovered = valueOf(service.handle(new RecoverPasswordCommand("rancher@example.com", "123456", "NewPassword1")));

        assertEquals("new-hash", recovered.getPasswordHash().value());
        assertTrue(stored.isConsumed());
    }

    @Test
    void recoverPasswordWithWeakPasswordDoesNotBurnTheCode() {
        var stored = code(VerificationPurpose.PASSWORD_RECOVERY, NOW.minusSeconds(60));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(AccountStatus.ACTIVE)));
        when(verificationCodeRepository.findLatestByUserIdAndPurpose(any(), any())).thenReturn(Optional.of(stored));

        var error = errorOf(service.handle(new RecoverPasswordCommand("rancher@example.com", "123456", "weak")));

        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals(0, stored.getFailedAttempts());
        verify(verificationCodeRepository, never()).save(any());
    }
}

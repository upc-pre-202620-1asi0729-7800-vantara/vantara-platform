package com.vantara.platform.iam.application.internal.commandservices;

import com.vantara.platform.iam.application.commandservices.Acknowledgement;
import com.vantara.platform.iam.application.commandservices.AuthenticatedUser;
import com.vantara.platform.iam.application.commandservices.UserCommandService;
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
import com.vantara.platform.iam.domain.model.valueobjects.RawPassword;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.iam.domain.repositories.VerificationCodeRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements the user use cases of the IAM context.
 *
 * <p>Security decisions applied here: sign-in answers the same error for an unknown email and a wrong
 * password, password recovery never reveals which emails are registered, and every verification
 * attempt is saved (even failed ones) so the attempt counter cannot be bypassed.</p>
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    /** Time to live of a verification code. */
    static final Duration CODE_TTL = Duration.ofMinutes(15);
    /** Minimum time between two codes requested for the same account and purpose. */
    static final Duration RESEND_INTERVAL = Duration.ofSeconds(60);

    static final ApplicationError INVALID_CREDENTIALS =
            new ApplicationError("INVALID_CREDENTIALS", "Invalid email or password");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final VerificationCodeGenerator codeGenerator;
    private final Clock clock;
    /** Hash compared against when the email is unknown, so both failures take the same time. */
    private final String dummyHash;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            VerificationCodeRepository verificationCodeRepository,
            HashingService hashingService,
            TokenService tokenService,
            EmailService emailService,
            VerificationCodeGenerator codeGenerator,
            Clock clock) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.codeGenerator = codeGenerator;
        this.clock = clock;
        this.dummyHash = hashingService.encode("not-a-real-password-1");
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            if (userRepository.existsByEmail(email)) {
                return Result.failure(ApplicationError.conflict("user", "The email address is already registered"));
            }
            var rawPassword = parse("password", () -> new RawPassword(command.password()));
            var requestedRole = parse("role", () -> Role.toRoleFromName(command.role()));
            var role = roleRepository.findByName(requestedRole.getName()).orElse(null);
            if (role == null) {
                return Result.failure(ApplicationError.notFound("role", requestedRole.getStringName()));
            }
            var passwordHash = new PasswordHash(hashingService.encode(rawPassword.value()));
            var user = userRepository.save(User.register(email, passwordHash, role, clock.instant()));
            var issued = issueCode(user, VerificationPurpose.EMAIL_VERIFICATION);
            emailService.sendEmailVerificationCode(email, issued.code(), issued.expiresAt());
            return Result.success(user);
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    @Override
    @Transactional
    public Result<AuthenticatedUser, ApplicationError> handle(SignInCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            if (command.password() == null || command.password().isBlank()) {
                return Result.failure(ApplicationError.validationError("password", "Password cannot be blank"));
            }
            var found = userRepository.findByEmail(email);
            var hash = found.map(u -> u.getPasswordHash().value()).orElse(dummyHash);
            var passwordMatches = hashingService.matches(command.password(), hash);
            if (found.isEmpty() || !passwordMatches) {
                return Result.failure(INVALID_CREDENTIALS);
            }
            var user = found.get();
            if (!user.canSignIn()) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "email-not-verified", "The email address has not been verified"));
            }
            user.recordSignIn(clock.instant());
            var saved = userRepository.save(user);
            var token = tokenService.generateToken(saved.getEmail().address());
            return Result.success(new AuthenticatedUser(saved, token));
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(VerifyEmailCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            var code = parse("code", () -> new VerificationCodeValue(command.code()));
            var user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                // Same answer as a wrong code: the response does not reveal if the account exists.
                return Result.failure(incorrectCode());
            }
            if (user.getStatus() == AccountStatus.ACTIVE) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "email-already-verified", "The email address is already verified"));
            }
            var failure = consumeCode(user, VerificationPurpose.EMAIL_VERIFICATION, code);
            if (failure.isPresent()) {
                return Result.failure(failure.get());
            }
            user.verifyEmail(clock.instant());
            return Result.success(userRepository.save(user));
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    @Override
    @Transactional
    public Result<Acknowledgement, ApplicationError> handle(SendVerificationCodeCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            var user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return Result.success(Acknowledgement.ACCEPTED);
            }
            if (user.getStatus() == AccountStatus.ACTIVE) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "email-already-verified", "The email address is already verified"));
            }
            if (issuedRecently(user, VerificationPurpose.EMAIL_VERIFICATION)) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "verification-code-resend", "Wait a minute before requesting another code"));
            }
            var issued = issueCode(user, VerificationPurpose.EMAIL_VERIFICATION);
            emailService.sendEmailVerificationCode(email, issued.code(), issued.expiresAt());
            return Result.success(Acknowledgement.ACCEPTED);
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    @Override
    @Transactional
    public Result<Acknowledgement, ApplicationError> handle(RequestPasswordRecoveryCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            var user = userRepository.findByEmail(email).orElse(null);
            // Unknown emails and rate-limited requests get the same success answer.
            if (user != null && !issuedRecently(user, VerificationPurpose.PASSWORD_RECOVERY)) {
                var issued = issueCode(user, VerificationPurpose.PASSWORD_RECOVERY);
                emailService.sendPasswordRecoveryCode(email, issued.code(), issued.expiresAt());
            }
            return Result.success(Acknowledgement.ACCEPTED);
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(RecoverPasswordCommand command) {
        try {
            var email = parse("email", () -> new EmailAddress(command.email()));
            var code = parse("code", () -> new VerificationCodeValue(command.code()));
            // The new password is validated first so a weak password does not burn a code attempt.
            var newPassword = parse("newPassword", () -> new RawPassword(command.newPassword()));
            var user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return Result.failure(incorrectCode());
            }
            var failure = consumeCode(user, VerificationPurpose.PASSWORD_RECOVERY, code);
            if (failure.isPresent()) {
                return Result.failure(failure.get());
            }
            user.recoverPassword(new PasswordHash(hashingService.encode(newPassword.value())), clock.instant());
            return Result.success(userRepository.save(user));
        } catch (InvalidInputException e) {
            return Result.failure(e.error());
        }
    }

    private record IssuedCode(VerificationCodeValue code, java.time.Instant expiresAt) {
    }

    private IssuedCode issueCode(User user, VerificationPurpose purpose) {
        var value = codeGenerator.generate();
        var verificationCode = VerificationCode.issue(
                new UserId(user.getId()), value, purpose, clock.instant(), CODE_TTL);
        var saved = verificationCodeRepository.save(verificationCode);
        return new IssuedCode(value, saved.getExpiresAt());
    }

    private boolean issuedRecently(User user, VerificationPurpose purpose) {
        return verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(user.getId()), purpose)
                .map(latest -> latest.getIssuedAt().plus(RESEND_INTERVAL).isAfter(clock.instant()))
                .orElse(false);
    }

    /**
     * Checks the candidate against the latest code of the user and always saves the code, because a
     * failed attempt changes its counter.
     *
     * @return the error to answer, or empty when the code was verified and consumed
     */
    private Optional<ApplicationError> consumeCode(User user, VerificationPurpose purpose, VerificationCodeValue candidate) {
        var latest = verificationCodeRepository.findLatestByUserIdAndPurpose(new UserId(user.getId()), purpose)
                .orElse(null);
        if (latest == null) {
            return Optional.of(ApplicationError.notFound("verification_code", purpose.name().toLowerCase()));
        }
        var outcome = latest.verify(candidate, clock.instant());
        verificationCodeRepository.save(latest);
        return switch (outcome) {
            case VERIFIED -> Optional.empty();
            case INVALID -> Optional.of(incorrectCode());
            case EXPIRED -> Optional.of(ApplicationError.businessRuleViolation(
                    "verification-code-expired", "The verification code has expired"));
            case ALREADY_USED -> Optional.of(ApplicationError.businessRuleViolation(
                    "verification-code-used", "The verification code was already used"));
            case ATTEMPTS_EXCEEDED -> Optional.of(ApplicationError.businessRuleViolation(
                    "verification-attempts-exceeded", "Too many wrong attempts, request a new code"));
        };
    }

    private static ApplicationError incorrectCode() {
        return ApplicationError.validationError("code", "The verification code is incorrect");
    }

    private static <T> T parse(String field, Supplier<T> factory) {
        try {
            return factory.get();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(ApplicationError.validationError(field, e.getMessage()));
        }
    }

    /** Carries a validation error out of the use case body. */
    private static final class InvalidInputException extends RuntimeException {
        private final transient ApplicationError error;

        InvalidInputException(ApplicationError error) {
            super(error.details(), null, false, false);
            this.error = error;
        }

        ApplicationError error() {
            return error;
        }
    }
}

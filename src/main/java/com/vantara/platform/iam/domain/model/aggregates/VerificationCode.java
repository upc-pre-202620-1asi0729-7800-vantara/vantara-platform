package com.vantara.platform.iam.domain.model.aggregates;

import com.vantara.platform.iam.domain.model.events.VerificationCodeIssuedEvent;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationOutcome;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root of a one-time code sent by email to verify an account or recover a password.
 *
 * <p>A six-digit code can be guessed by brute force, so the aggregate limits the number of wrong
 * attempts to {@value #MAX_FAILED_ATTEMPTS}.</p>
 */
public class VerificationCode extends AbstractDomainAggregateRoot<VerificationCode> {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private Long id;
    private UserId userId;
    private VerificationCodeValue code;
    private VerificationPurpose purpose;
    private Instant issuedAt;
    private Instant expiresAt;
    private Instant consumedAt;
    private int failedAttempts;

    private VerificationCode() {
    }

    /**
     * Factory method that issues a new code for a user ("send email code").
     *
     * @param userId  owner of the code
     * @param code    generated code value
     * @param purpose reason why the code is issued
     * @param now     issue instant
     * @param ttl     time to live of the code
     * @return the new code with a {@link VerificationCodeIssuedEvent} recorded
     */
    public static VerificationCode issue(
            UserId userId,
            VerificationCodeValue code,
            VerificationPurpose purpose,
            Instant now,
            Duration ttl
    ) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(purpose, "Purpose cannot be null");
        Objects.requireNonNull(now, "Issue instant cannot be null");
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("Time to live must be positive");
        }

        var verificationCode = new VerificationCode();
        verificationCode.userId = userId;
        verificationCode.code = code;
        verificationCode.purpose = purpose;
        verificationCode.issuedAt = now;
        verificationCode.expiresAt = now.plus(ttl);
        verificationCode.registerDomainEvent(new VerificationCodeIssuedEvent(
                userId.userId(), purpose.name(), verificationCode.expiresAt, now));
        return verificationCode;
    }

    /**
     * Rebuilds a verification code from its persisted state. It does not record any domain event.
     */
    public static VerificationCode reconstitute(
            Long id,
            UserId userId,
            VerificationCodeValue code,
            VerificationPurpose purpose,
            Instant issuedAt,
            Instant expiresAt,
            Instant consumedAt,
            int failedAttempts
    ) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("Invalid verification code id");
        }
        if (failedAttempts < 0) {
            throw new IllegalArgumentException("Failed attempts cannot be negative");
        }

        var verificationCode = new VerificationCode();
        verificationCode.id = id;
        verificationCode.userId = Objects.requireNonNull(userId, "User id cannot be null");
        verificationCode.code = Objects.requireNonNull(code, "Code cannot be null");
        verificationCode.purpose = Objects.requireNonNull(purpose, "Purpose cannot be null");
        verificationCode.issuedAt = Objects.requireNonNull(issuedAt, "Issued instant cannot be null");
        verificationCode.expiresAt = Objects.requireNonNull(expiresAt, "Expiration instant cannot be null");
        verificationCode.consumedAt = consumedAt;
        verificationCode.failedAttempts = failedAttempts;
        return verificationCode;
    }

    /**
     * Checks a candidate against this code ("code received").
     *
     * <p>On success the code is consumed. On a wrong candidate the attempt is counted. The caller
     * must persist the aggregate whatever the outcome is.</p>
     *
     * @param candidate code typed by the user
     * @param now       verification instant
     * @return what happened with the candidate
     */
    public VerificationOutcome verify(VerificationCodeValue candidate, Instant now) {
        if (isConsumed()) {
            return VerificationOutcome.ALREADY_USED;
        }
        if (isExpired(now)) {
            return VerificationOutcome.EXPIRED;
        }
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            return VerificationOutcome.ATTEMPTS_EXCEEDED;
        }
        if (!sameCode(candidate)) {
            failedAttempts++;
            return VerificationOutcome.INVALID;
        }
        this.consumedAt = now;
        return VerificationOutcome.VERIFIED;
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    /** Compares in constant time so the comparison does not leak how many digits matched. */
    private boolean sameCode(VerificationCodeValue candidate) {
        if (candidate == null) {
            return false;
        }
        return MessageDigest.isEqual(
                code.value().getBytes(StandardCharsets.UTF_8),
                candidate.value().getBytes(StandardCharsets.UTF_8));
    }

    public Long getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public VerificationCodeValue getCode() {
        return code;
    }

    public VerificationPurpose getPurpose() {
        return purpose;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }
}

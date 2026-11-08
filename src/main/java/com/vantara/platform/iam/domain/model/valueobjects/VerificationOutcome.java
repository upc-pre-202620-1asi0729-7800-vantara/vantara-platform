package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Result of checking a candidate code against a verification code.
 *
 * <p>It is a value and not an exception on purpose: a failed attempt must be persisted (the attempt
 * counter changes), so the use case can save the aggregate and then translate the outcome.</p>
 */
public enum VerificationOutcome {
    /** The candidate matched and the code was consumed. */
    VERIFIED,
    /** The candidate did not match; the attempt was counted. */
    INVALID,
    /** The code is past its expiration instant. */
    EXPIRED,
    /** The code was already consumed. */
    ALREADY_USED,
    /** Too many wrong attempts; the code can no longer be used. */
    ATTEMPTS_EXCEEDED
}

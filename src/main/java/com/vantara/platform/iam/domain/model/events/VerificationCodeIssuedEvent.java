package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a verification code is issued for a user ("email sent" / "password requested").
 *
 * <p>The event is a fact and never carries the code itself: the secret is only known to the
 * issuing use case, which is the one that delivers it by email.</p>
 *
 * @param userId     identifier of the user the code belongs to
 * @param purpose    name of the purpose the code was issued for
 * @param expiresAt  instant at which the code stops being valid
 * @param occurredAt instant at which the code was issued
 */
public record VerificationCodeIssuedEvent(
        Long userId,
        String purpose,
        Instant expiresAt,
        Instant occurredAt) implements DomainEvent {
}

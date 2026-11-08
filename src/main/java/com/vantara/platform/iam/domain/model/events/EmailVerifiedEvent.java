package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a user verifies the email address and the account is approved ("email verified").
 *
 * @param email      verified email address
 * @param occurredAt instant at which the verification happened
 */
public record EmailVerifiedEvent(String email, Instant occurredAt) implements DomainEvent {
}

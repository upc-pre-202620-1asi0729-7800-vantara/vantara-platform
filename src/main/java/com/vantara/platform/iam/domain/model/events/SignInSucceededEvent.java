package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a user signs in with valid credentials ("log in successful").
 *
 * @param email      email address of the user
 * @param occurredAt instant at which the sign-in happened
 */
public record SignInSucceededEvent(String email, Instant occurredAt) implements DomainEvent {
}

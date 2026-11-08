package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a user sets a new password through the recovery flow ("password recovered").
 *
 * @param email      email address of the user
 * @param occurredAt instant at which the password was replaced
 */
public record PasswordRecoveredEvent(String email, Instant occurredAt) implements DomainEvent {
}

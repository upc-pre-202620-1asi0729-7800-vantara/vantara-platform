package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a new user registers in the platform ("user registered").
 *
 * @param email      email address of the new user
 * @param role       role requested at registration
 * @param occurredAt instant at which the registration happened
 */
public record UserSignedUpEvent(String email, String role, Instant occurredAt) implements DomainEvent {
}

package com.vantara.platform.iam.domain.model.events;

import com.vantara.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised when a visitor submits the contact form ("contact request sent").
 *
 * @param fullName   name of the person who contacted Vantara
 * @param email      email address of the requester
 * @param occurredAt instant at which the request was sent
 */
public record ContactRequestSentEvent(String fullName, String email, Instant occurredAt) implements DomainEvent {
}

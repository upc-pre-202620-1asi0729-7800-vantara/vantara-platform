package com.vantara.platform.iam.domain.model.aggregates;

import com.vantara.platform.iam.domain.model.events.ContactRequestSentEvent;
import com.vantara.platform.iam.domain.model.valueobjects.ContactMessage;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root of a contact form submitted by a visitor or a user.
 */
public class ContactRequest extends AbstractDomainAggregateRoot<ContactRequest> {

    private Long id;
    private FullName fullName;
    private EmailAddress email;
    private ContactMessage message;
    private Instant sentAt;

    private ContactRequest() {
    }

    /**
     * Factory method that creates a contact request ("send contact form").
     *
     * @return the new request with a {@link ContactRequestSentEvent} recorded
     */
    public static ContactRequest send(FullName fullName, EmailAddress email, ContactMessage message, Instant now) {
        Objects.requireNonNull(fullName, "Full name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(now, "Sent instant cannot be null");

        var request = new ContactRequest();
        request.fullName = fullName;
        request.email = email;
        request.message = message;
        request.sentAt = now;
        request.registerDomainEvent(new ContactRequestSentEvent(fullName.value(), email.address(), now));
        return request;
    }

    /**
     * Rebuilds a contact request from its persisted state. It does not record any domain event.
     */
    public static ContactRequest reconstitute(
            Long id,
            FullName fullName,
            EmailAddress email,
            ContactMessage message,
            Instant sentAt
    ) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("Invalid contact request id");
        }
        var request = new ContactRequest();
        request.id = id;
        request.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        request.email = Objects.requireNonNull(email, "Email cannot be null");
        request.message = Objects.requireNonNull(message, "Message cannot be null");
        request.sentAt = Objects.requireNonNull(sentAt, "Sent instant cannot be null");
        return request;
    }

    public Long getId() {
        return id;
    }

    public FullName getFullName() {
        return fullName;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public ContactMessage getMessage() {
        return message;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}

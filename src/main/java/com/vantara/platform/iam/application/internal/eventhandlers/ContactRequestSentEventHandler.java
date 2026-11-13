package com.vantara.platform.iam.application.internal.eventhandlers;

import com.vantara.platform.iam.application.internal.outboundservices.email.EmailService;
import com.vantara.platform.iam.domain.model.events.ContactRequestSentEvent;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends the confirmation email once a contact request is committed. A mail failure must not undo
 * the stored request, so it is only logged.
 */
@Component
public class ContactRequestSentEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactRequestSentEventHandler.class);

    private final EmailService emailService;

    public ContactRequestSentEventHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ContactRequestSentEvent event) {
        try {
            emailService.sendContactConfirmation(new EmailAddress(event.email()), new FullName(event.fullName()));
        } catch (RuntimeException e) {
            LOGGER.error("Contact confirmation email could not be sent", e);
        }
    }
}

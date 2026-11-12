package com.vantara.platform.iam.application.internal.eventhandlers;

import com.vantara.platform.iam.domain.model.events.VerificationCodeIssuedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Leaves an audit trace when a code is issued. The event carries no secret, so the code itself is
 * delivered by the use case that generated it and never by this handler.
 */
@Component
public class VerificationCodeIssuedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationCodeIssuedEventHandler.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(VerificationCodeIssuedEvent event) {
        LOGGER.info("Verification code issued for user {} ({}), expires at {}",
                event.userId(), event.purpose(), event.expiresAt());
    }
}

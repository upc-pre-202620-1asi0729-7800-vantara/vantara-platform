package com.vantara.platform.iam.infrastructure.email.logging;

import com.vantara.platform.iam.application.internal.outboundservices.email.EmailService;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Placeholder email adapter that writes to the log instead of sending real emails, until an email
 * provider is chosen. The addresses are masked and the codes are only written when
 * {@code notifications.email.log-codes=true}, which must stay off outside local development.
 */
@Service
public class LoggingEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailService.class);

    private final boolean logCodes;

    public LoggingEmailService(@Value("${notifications.email.log-codes:false}") boolean logCodes) {
        this.logCodes = logCodes;
    }

    @Override
    public void sendEmailVerificationCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt) {
        log("email verification", to, code, expiresAt);
    }

    @Override
    public void sendPasswordRecoveryCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt) {
        log("password recovery", to, code, expiresAt);
    }

    @Override
    public void sendContactConfirmation(EmailAddress to, FullName name) {
        LOGGER.info("[email] contact confirmation to {}", mask(to));
    }

    private void log(String kind, EmailAddress to, VerificationCodeValue code, Instant expiresAt) {
        if (logCodes) {
            LOGGER.warn("[email] {} code {} to {} (expires {})", kind, code.value(), mask(to), expiresAt);
        } else {
            LOGGER.info("[email] {} code to {} (expires {})", kind, mask(to), expiresAt);
        }
    }

    private static String mask(EmailAddress email) {
        var address = email.address();
        var at = address.indexOf('@');
        return address.charAt(0) + "***" + address.substring(at);
    }
}

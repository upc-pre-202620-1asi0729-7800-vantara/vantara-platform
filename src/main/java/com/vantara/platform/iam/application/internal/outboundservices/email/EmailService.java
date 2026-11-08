package com.vantara.platform.iam.application.internal.outboundservices.email;

import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;

import java.time.Instant;

/**
 * Outbound port that delivers the emails sent by the IAM context.
 *
 * <p>Verification codes are secrets: implementations must never write them to logs in production.</p>
 */
public interface EmailService {

    void sendEmailVerificationCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt);

    void sendPasswordRecoveryCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt);

    void sendContactConfirmation(EmailAddress to, FullName name);
}

package com.vantara.platform.iam.infrastructure.codes.secure;

import com.vantara.platform.iam.application.internal.outboundservices.codes.VerificationCodeGenerator;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates six-digit codes with a cryptographically secure random source.
 */
@Component
public class SecureRandomVerificationCodeGenerator implements VerificationCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public VerificationCodeValue generate() {
        return new VerificationCodeValue("%06d".formatted(random.nextInt(1_000_000)));
    }
}

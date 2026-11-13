package com.vantara.platform.iam.application.internal.outboundservices.codes;

import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;

/**
 * Outbound port that generates unpredictable verification codes. Generation is not a domain rule.
 */
public interface VerificationCodeGenerator {

    VerificationCodeValue generate();
}

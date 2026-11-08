package com.vantara.platform.iam.domain.repositories;

import com.vantara.platform.iam.domain.model.aggregates.VerificationCode;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;

import java.util.Optional;

/**
 * Domain repository contract for {@link VerificationCode} aggregates.
 */
public interface VerificationCodeRepository {

    VerificationCode save(VerificationCode verificationCode);

    /**
     * @return the most recently issued code of the user for the given purpose
     */
    Optional<VerificationCode> findLatestByUserIdAndPurpose(UserId userId, VerificationPurpose purpose);
}

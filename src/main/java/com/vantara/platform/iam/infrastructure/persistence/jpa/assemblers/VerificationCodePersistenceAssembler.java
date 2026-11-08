package com.vantara.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.iam.domain.model.aggregates.VerificationCode;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.VerificationCodePersistenceEntity;

/**
 * Translates between the {@link VerificationCode} aggregate and its persistence entity.
 */
public final class VerificationCodePersistenceAssembler {

    private VerificationCodePersistenceAssembler() {
    }

    public static VerificationCode toDomainFromPersistence(VerificationCodePersistenceEntity entity) {
        return VerificationCode.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getCode(),
                entity.getPurpose(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                entity.getConsumedAt(),
                entity.getFailedAttempts());
    }

    /** Copies the aggregate into {@code target}: a new entity, or the stored one when it already exists. */
    public static VerificationCodePersistenceEntity toPersistenceFromDomain(
            VerificationCodePersistenceEntity target, VerificationCode code) {
        target.setUserId(code.getUserId());
        target.setCode(code.getCode());
        target.setPurpose(code.getPurpose());
        target.setIssuedAt(code.getIssuedAt());
        target.setExpiresAt(code.getExpiresAt());
        target.setConsumedAt(code.getConsumedAt());
        target.setFailedAttempts(code.getFailedAttempts());
        return target;
    }
}

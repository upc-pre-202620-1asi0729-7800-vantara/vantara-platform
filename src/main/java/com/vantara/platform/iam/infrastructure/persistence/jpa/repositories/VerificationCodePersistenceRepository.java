package com.vantara.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.iam.infrastructure.persistence.jpa.entities.VerificationCodePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodePersistenceRepository extends JpaRepository<VerificationCodePersistenceEntity, Long> {

    Optional<VerificationCodePersistenceEntity> findFirstByUserIdAndPurposeOrderByIssuedAtDescIdDesc(
            UserId userId, VerificationPurpose purpose);
}

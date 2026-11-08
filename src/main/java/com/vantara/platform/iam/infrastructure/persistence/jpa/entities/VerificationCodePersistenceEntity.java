package com.vantara.platform.iam.infrastructure.persistence.jpa.entities;

import com.vantara.platform.iam.infrastructure.persistence.jpa.converters.UserIdPersistenceConverter;
import com.vantara.platform.iam.infrastructure.persistence.jpa.converters.VerificationCodeValuePersistenceConverter;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * JPA persistence entity of a verification code. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "verification_codes",
        indexes = @Index(name = "idx_verification_codes_user_purpose", columnList = "user_id, purpose"))
public class VerificationCodePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(nullable = false)
    private UserId userId;

    @Convert(converter = VerificationCodeValuePersistenceConverter.class)
    @Column(nullable = false, length = 6)
    private VerificationCodeValue code;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private VerificationPurpose purpose;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant consumedAt;

    @Column(nullable = false)
    private int failedAttempts;
}

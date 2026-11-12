package com.vantara.platform.iam.infrastructure.persistence.jpa.entities;

import com.vantara.platform.iam.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.vantara.platform.iam.infrastructure.persistence.jpa.converters.PasswordHashPersistenceConverter;
import com.vantara.platform.iam.domain.model.valueobjects.AccountStatus;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA persistence entity of a user account. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false, unique = true, length = 254)
    private EmailAddress email;

    @Convert(converter = PasswordHashPersistenceConverter.class)
    @Column(nullable = false)
    private PasswordHash passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private AccountStatus status;

    private Instant lastSignInAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RolePersistenceEntity> roles = new HashSet<>();
}

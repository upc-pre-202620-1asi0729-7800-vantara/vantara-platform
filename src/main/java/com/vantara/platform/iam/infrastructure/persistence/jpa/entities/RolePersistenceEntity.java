package com.vantara.platform.iam.infrastructure.persistence.jpa.entities;

import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JPA persistence entity of a role. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, unique = true, length = 30)
    private Roles name;
}

package com.vantara.platform.profilemanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

/**
 * JPA persistence entity of a profile. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile_profile",
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_profiles_user", columnNames = "user_id"))
public class ProfileJpaEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "organization_name", length = 120)
    private String organizationName;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "theme", nullable = false, length = 10)
    private String theme;

    @Column(name = "locale", nullable = false, length = 5)
    private String locale;
}

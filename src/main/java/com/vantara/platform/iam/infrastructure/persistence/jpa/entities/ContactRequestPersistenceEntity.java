package com.vantara.platform.iam.infrastructure.persistence.jpa.entities;

import com.vantara.platform.iam.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity of a contact request. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contact_requests")
public class ContactRequestPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 120)
    private String fullName;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false, length = 254)
    private EmailAddress email;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private Instant sentAt;
}

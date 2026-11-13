package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentOrigin;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AppointmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA persistence entity of a veterinary appointment. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointments")
public class AppointmentPersistenceEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false)
    private Long rancherId;

    @Column(nullable = false)
    private Long veterinarianId;

    @Column(nullable = false)
    private Long requestedBy;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AppointmentOrigin origin;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(length = 255)
    private String cancellationReason;

    @Column(nullable = false)
    private LocalDate createdAt;
}

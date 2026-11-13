package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalHealthStatus;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.MedicalRecordSeverity;
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

import java.time.LocalDateTime;

/**
 * JPA persistence entity of a clinical record. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "medical_records")
public class MedicalRecordPersistenceEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(length = 36)
    private String appointmentId;

    @Column(nullable = false, length = 36)
    private String animalId;

    @Column(nullable = false)
    private Long veterinarianId;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Column(nullable = false, length = 1000)
    private String symptoms;

    @Column(nullable = false, length = 1000)
    private String diagnosis;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private MedicalRecordSeverity severity;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AnimalHealthStatus healthStatus;

    @Column(length = 1000)
    private String notes;
}

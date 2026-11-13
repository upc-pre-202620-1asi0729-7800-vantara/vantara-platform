package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.TreatmentStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA persistence entity of a treatment. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "treatments")
public class TreatmentPersistenceEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String medicalRecordId;

    @Column(nullable = false, length = 36)
    private String animalId;

    @Column(nullable = false, length = 36)
    private String medicationId;

    @Column(nullable = false, length = 64)
    private String treatmentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal doseAmount;

    @Column(nullable = false, length = 16)
    private String doseUnit;

    @Column(nullable = false, length = 64)
    private String route;

    @Column(nullable = false)
    private int frequencyHours;

    @Column(nullable = false)
    private int durationDays;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime administeredAt;

    private LocalDate nextDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private TreatmentStatus status;

    @Column(length = 1000)
    private String instructions;
}

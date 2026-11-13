package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.VaccineStatus;
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

/**
 * JPA persistence entity of a vaccine dose. It holds no business logic.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vaccines")
public class VaccinePersistenceEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String animalId;

    @Column(nullable = false)
    private Long veterinarianId;

    @Column(length = 64)
    private String vaccineType;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal doseAmount;

    @Column(nullable = false, length = 16)
    private String doseUnit;

    @Column(length = 120)
    private String laboratory;

    @Column(length = 64)
    private String lotNumber;

    @Column(nullable = false)
    private LocalDate appliedAt;

    private LocalDate nextDoseAt;

    @Column(length = 64)
    private String route;

    @Column(length = 120)
    private String applicationSite;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private VaccineStatus status;

    @Column(length = 1000)
    private String observations;
}

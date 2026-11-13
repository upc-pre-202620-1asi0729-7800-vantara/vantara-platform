package com.vantara.platform.reproductive.infrastructure.persistence.jpa.entities;

import com.vantara.platform.reproductive.domain.model.valueobjects.ReproductiveStatus;
import com.vantara.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA persistence entity for reproductive records.
 */
@Entity
@Table(name = "reproductive_records")
@Getter
@Setter
@NoArgsConstructor
public class ReproductiveRecordPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "animal_id", nullable = false, length = 36)
    private String animalId;

    @Column(name = "confirmed_on", nullable = false)
    private LocalDate confirmedOn;

    @Column(name = "expected_calving_on")
    private LocalDate expectedCalvingOn;

    @Column(name = "dry_off_on")
    private LocalDate dryOffOn;

    @Column(name = "calved_on")
    private LocalDate calvedOn;

    @Column(name = "calving_outcome", length = 120)
    private String calvingOutcome;

    @Column(name = "calf_id", length = 36)
    private String calfId;

    @Column(name = "birth_weight_kg", precision = 7, scale = 2)
    private BigDecimal birthWeightKg;

    @Column(name = "weaned_on")
    private LocalDate weanedOn;

    @Column(name = "weaning_weight_kg", precision = 7, scale = 2)
    private BigDecimal weaningWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReproductiveStatus status;
}

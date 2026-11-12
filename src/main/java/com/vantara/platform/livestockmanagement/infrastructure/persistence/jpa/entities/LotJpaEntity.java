package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lots")
public class LotJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 64) private String rancherId;
    @Column(nullable = false, length = 128) private String name;
    @Column(nullable = false, length = 512) private String purpose;
    @Column(nullable = false, length = 32) private String status;
    protected LotJpaEntity() { }
    private LotJpaEntity(Lot lot) { id=lot.getId(); rancherId=lot.getRancherId(); name=lot.getName(); purpose=lot.getPurpose(); status=lot.getStatus(); }
    public static LotJpaEntity fromDomain(Lot lot) { return new LotJpaEntity(lot); }
    public Lot toDomain() { return Lot.register(id, rancherId, name, purpose, status); }
}

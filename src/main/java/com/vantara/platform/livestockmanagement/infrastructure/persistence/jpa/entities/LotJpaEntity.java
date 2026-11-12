
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lots")
public class LotJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 64)
    private String rancherId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 512)
    private String purpose;

    @Column(nullable = false, length = 32)
    private String status;

    protected LotJpaEntity() {
    }

    public LotJpaEntity(
            String id,
            String rancherId,
            String name,
            String purpose,
            String status
    ) {
        this.id = id;
        this.rancherId = rancherId;
        this.name = name;
        this.purpose = purpose;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getRancherId() {
        return rancherId;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }
}

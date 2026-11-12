
package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "animals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_animal_rancher_ear_tag",
                        columnNames = {"rancher_id", "ear_tag"}
                )
        }
)
public class AnimalJpaEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "rancher_id", nullable = false, length = 64)
    private String rancherId;

    @Column(name = "lot_id", nullable = false, length = 36)
    private String lotId;

    @Column(name = "ear_tag", nullable = false, length = 64)
    private String earTag;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 64)
    private String species;

    @Column(nullable = false, length = 128)
    private String breed;

    @Column(nullable = false, length = 32)
    private String sex;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 36)
    private String motherId;

    @Column(length = 36)
    private String fatherId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(nullable = false)
    private LocalDate registeredAt;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String photoUrl;

    private LocalDate weanedOn;

    protected AnimalJpaEntity() {
    }

    public AnimalJpaEntity(
            String id,
            String rancherId,
            String lotId,
            String earTag,
            String name,
            String species,
            String breed,
            String sex,
            LocalDate birthDate,
            String motherId,
            String fatherId,
            BigDecimal weight,
            String status,
            LocalDate registeredAt,
            String photoUrl,
            LocalDate weanedOn
    ) {
        this.id = id;
        this.rancherId = rancherId;
        this.lotId = lotId;
        this.earTag = earTag;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.sex = sex;
        this.birthDate = birthDate;
        this.motherId = motherId;
        this.fatherId = fatherId;
        this.weight = weight;
        this.status = status;
        this.registeredAt = registeredAt;
        this.photoUrl = photoUrl;
        this.weanedOn = weanedOn;
    }

    public String getId() {
        return id;
    }

    public String getRancherId() {
        return rancherId;
    }

    public String getLotId() {
        return lotId;
    }

    public String getEarTag() {
        return earTag;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getSex() {
        return sex;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getMotherId() {
        return motherId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public LocalDate getWeanedOn() {
        return weanedOn;
    }
}

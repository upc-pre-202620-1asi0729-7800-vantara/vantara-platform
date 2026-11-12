package com.vantara.platform.livestockmanagement.infrastructure.persistence.jpa.entities;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "animals")
public class AnimalJpaEntity {
    @Id @Column(length = 36) private String id;
    @Column(nullable = false, length = 64) private String rancherId;
    @Column(nullable = false, length = 36) private String lotId;
    @Column(nullable = false, unique = true, length = 64) private String earTag;
    @Column(nullable = false, length = 128) private String name;
    @Column(nullable = false, length = 64) private String species;
    @Column(nullable = false, length = 128) private String breed;
    @Column(nullable = false, length = 32) private String sex;
    @Column(nullable = false) private LocalDate birthDate;
    @Column(length = 36) private String motherId;
    @Column(length = 36) private String fatherId;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal weight;
    @Column(nullable = false, length = 32) private String status;
    @Column(nullable = false) private LocalDate registeredAt;
    @Column(length = 2048) private String photoUrl;
    private LocalDate weanedOn;
    protected AnimalJpaEntity() { }
    private AnimalJpaEntity(Animal animal) { id=animal.getId(); rancherId=animal.getRancherId(); lotId=animal.getLotId(); earTag=animal.getEarTag(); name=animal.getName(); species=animal.getSpecies(); breed=animal.getBreed(); sex=animal.getSex(); birthDate=animal.getBirthDate(); motherId=animal.getMotherId(); fatherId=animal.getFatherId(); weight=animal.getWeight(); status=animal.getStatus(); registeredAt=animal.getRegisteredAt(); photoUrl=animal.getPhotoUrl(); weanedOn=animal.getWeanedOn(); }
    public static AnimalJpaEntity fromDomain(Animal animal) { return new AnimalJpaEntity(animal); }
    public Animal toDomain() { return Animal.register(id, rancherId, lotId, earTag, name, species, breed, sex, birthDate, motherId, fatherId, weight, status, registeredAt, photoUrl, weanedOn); }
}

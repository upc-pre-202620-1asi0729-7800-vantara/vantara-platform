package com.vantara.platform.livestockmanagement.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Represents an animal registered in a rancher's livestock inventory. */
public final class Animal extends AbstractDomainAggregateRoot<Animal> {
    private final String id;
    private final String rancherId;
    private String lotId;
    private final String earTag;
    private String name;
    private String species;
    private String breed;
    private String sex;
    private LocalDate birthDate;
    private String motherId;
    private String fatherId;
    private BigDecimal weight;
    private String status;
    private final LocalDate registeredAt;
    private String photoUrl;
    private LocalDate weanedOn;

    private Animal(String id, String rancherId, String lotId, String earTag, String name, String species,
                   String breed, String sex, LocalDate birthDate, String motherId, String fatherId,
                   BigDecimal weight, String status, LocalDate registeredAt, String photoUrl, LocalDate weanedOn) {
        this.id = requireText(id, "Animal id");
        this.rancherId = requireText(rancherId, "Rancher id");
        this.lotId = requireText(lotId, "Lot id");
        this.earTag = requireText(earTag, "Ear tag");
        this.name = requireText(name, "Animal name");
        this.species = requireText(species, "Species");
        this.breed = requireText(breed, "Breed");
        this.sex = requireText(sex, "Sex");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.motherId = motherId;
        this.fatherId = fatherId;
        this.weight = requireNonNegative(weight, "Weight");
        this.status = requireText(status, "Status");
        this.registeredAt = Objects.requireNonNull(registeredAt, "Registration date cannot be null");
        this.photoUrl = photoUrl == null ? "" : photoUrl;
        this.weanedOn = weanedOn;
    }

    public static Animal register(String id, String rancherId, String lotId, String earTag, String name,
                                  String species, String breed, String sex, LocalDate birthDate, String motherId,
                                  String fatherId, BigDecimal weight, String status, LocalDate registeredAt,
                                  String photoUrl, LocalDate weanedOn) {
        return new Animal(id, rancherId, lotId, earTag, name, species, breed, sex, birthDate, motherId, fatherId,
                weight, status, registeredAt, photoUrl, weanedOn);
    }

    public void update(String lotId, String name, String species, String breed, String sex, LocalDate birthDate,
                       String motherId, String fatherId, BigDecimal weight, String status, String photoUrl,
                       LocalDate weanedOn) {
        this.lotId = requireText(lotId, "Lot id");
        this.name = requireText(name, "Animal name");
        this.species = requireText(species, "Species");
        this.breed = requireText(breed, "Breed");
        this.sex = requireText(sex, "Sex");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.motherId = motherId;
        this.fatherId = fatherId;
        this.weight = requireNonNegative(weight, "Weight");
        this.status = requireText(status, "Status");
        this.photoUrl = photoUrl == null ? "" : photoUrl;
        this.weanedOn = weanedOn;
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " cannot be blank");
        return value.trim();
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String label) {
        if (value == null || value.signum() < 0) throw new IllegalArgumentException(label + " must be non-negative");
        return value;
    }

    public String getId() { return id; }
    public String getRancherId() { return rancherId; }
    public String getLotId() { return lotId; }
    public String getEarTag() { return earTag; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public String getSex() { return sex; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getMotherId() { return motherId; }
    public String getFatherId() { return fatherId; }
    public BigDecimal getWeight() { return weight; }
    public String getStatus() { return status; }
    public LocalDate getRegisteredAt() { return registeredAt; }
    public String getPhotoUrl() { return photoUrl; }
    public LocalDate getWeanedOn() { return weanedOn; }
}

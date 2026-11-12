
package com.vantara.platform.livestockmanagement.domain.model.aggregates;

import com.vantara.platform.livestockmanagement.domain.model.valueobjects.AnimalWeight;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.EarTag;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an animal registered in a rancher's livestock inventory.
 */
public final class Animal extends AbstractDomainAggregateRoot<Animal> {

    private final String id;
    private final String rancherId;
    private String lotId;
    private final EarTag earTag;
    private String name;
    private String species;
    private String breed;
    private String sex;
    private LocalDate birthDate;
    private String motherId;
    private String fatherId;
    private AnimalWeight weight;
    private String status;
    private final LocalDate registeredAt;
    private String photoUrl;
    private LocalDate weanedOn;

    private Animal(
            String id,
            String rancherId,
            String lotId,
            EarTag earTag,
            String name,
            String species,
            String breed,
            String sex,
            LocalDate birthDate,
            String motherId,
            String fatherId,
            AnimalWeight weight,
            String status,
            LocalDate registeredAt,
            String photoUrl,
            LocalDate weanedOn
    ) {
        this.id = requireText(id, "Animal id");
        this.rancherId = requireText(rancherId, "Rancher id");
        this.lotId = requireText(lotId, "Lot id");
        this.earTag = Objects.requireNonNull(earTag, "Ear tag cannot be null");
        this.name = requireText(name, "Animal name");
        this.species = requireText(species, "Species");
        this.breed = requireText(breed, "Breed");
        this.sex = requireText(sex, "Sex");

        this.birthDate = Objects.requireNonNull(
                birthDate, "Birth date cannot be null"
        );

        this.registeredAt = Objects.requireNonNull(
                registeredAt, "Registration date cannot be null"
        );

        this.motherId = optionalText(motherId);
        this.fatherId = optionalText(fatherId);

        validateDates(this.birthDate, this.registeredAt, weanedOn);
        validateParentIds(this.id, this.motherId, this.fatherId);

        this.weight = Objects.requireNonNull(weight, "Weight cannot be null");
        this.status = requireText(status, "Status");
        this.photoUrl = optionalText(photoUrl) == null ? "" : optionalText(photoUrl);
        this.weanedOn = weanedOn;
    }

    public static Animal register(
            String id,
            String rancherId,
            String lotId,
            EarTag earTag,
            String name,
            String species,
            String breed,
            String sex,
            LocalDate birthDate,
            String motherId,
            String fatherId,
            AnimalWeight weight,
            String status,
            LocalDate registeredAt,
            String photoUrl,
            LocalDate weanedOn
    ) {
        return new Animal(
                id, rancherId, lotId, earTag, name, species, breed,
                sex, birthDate, motherId, fatherId, weight, status,
                registeredAt, photoUrl, weanedOn
        );
    }

    public void update(
            String lotId,
            String name,
            String species,
            String breed,
            String sex,
            LocalDate birthDate,
            String motherId,
            String fatherId,
            AnimalWeight weight,
            String status,
            String photoUrl,
            LocalDate weanedOn
    ) {
        LocalDate validatedBirthDate = Objects.requireNonNull(
                birthDate, "Birth date cannot be null"
        );

        String validatedMotherId = optionalText(motherId);
        String validatedFatherId = optionalText(fatherId);

        AnimalWeight validatedWeight = Objects.requireNonNull(
                weight, "Weight cannot be null"
        );

        validateDates(validatedBirthDate, registeredAt, weanedOn);
        validateParentIds(id, validatedMotherId, validatedFatherId);

        this.lotId = requireText(lotId, "Lot id");
        this.name = requireText(name, "Animal name");
        this.species = requireText(species, "Species");
        this.breed = requireText(breed, "Breed");
        this.sex = requireText(sex, "Sex");
        this.birthDate = validatedBirthDate;
        this.motherId = validatedMotherId;
        this.fatherId = validatedFatherId;
        this.weight = validatedWeight;
        this.status = requireText(status, "Status");
        this.photoUrl = optionalText(photoUrl) == null ? "" : optionalText(photoUrl);
        this.weanedOn = weanedOn;
    }

    /**
     * Moves the animal after the movement aggregate has validated its destination.
     */
    public void assignToLot(String lotId) {
        this.lotId = requireText(lotId, "Lot id");
    }

    private static void validateDates(
            LocalDate birthDate,
            LocalDate registeredAt,
            LocalDate weanedOn
    ) {
        if (birthDate.isAfter(registeredAt)) {
            throw new IllegalArgumentException(
                    "Birth date cannot be after registration date"
            );
        }

        if (weanedOn != null && weanedOn.isBefore(birthDate)) {
            throw new IllegalArgumentException(
                    "Weaning date cannot be before birth date"
            );
        }
    }

    private static void validateParentIds(
            String animalId,
            String motherId,
            String fatherId
    ) {
        if (animalId.equals(motherId) || animalId.equals(fatherId)) {
            throw new IllegalArgumentException(
                    "An animal cannot be its own parent"
            );
        }

        if (motherId != null && motherId.equals(fatherId)) {
            throw new IllegalArgumentException(
                    "Mother and father cannot be the same animal"
            );
        }
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getId() { return id; }
    public String getRancherId() { return rancherId; }
    public String getLotId() { return lotId; }
    public EarTag getEarTag() { return earTag; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public String getSex() { return sex; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getMotherId() { return motherId; }
    public String getFatherId() { return fatherId; }
    public AnimalWeight getWeight() { return weight; }
    public String getStatus() { return status; }
    public LocalDate getRegisteredAt() { return registeredAt; }
    public String getPhotoUrl() { return photoUrl; }
    public LocalDate getWeanedOn() { return weanedOn; }
}

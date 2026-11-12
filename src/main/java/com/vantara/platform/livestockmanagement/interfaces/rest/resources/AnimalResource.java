package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalResource(
        String id, @NotBlank String rancherId, @NotBlank String lotId, @NotBlank String earTag, @NotBlank String name,
        @NotBlank String species, @NotBlank String breed, @NotBlank String sex, @NotNull LocalDate birthDate,
        String motherId, String fatherId, @NotNull @DecimalMin("0") BigDecimal weight, @NotBlank String status,
        @NotNull LocalDate registeredAt, String photoUrl, LocalDate weanedOn) {
    public Animal toDomain(String generatedId) { return Animal.register(id == null || id.isBlank() ? generatedId : id, rancherId, lotId, earTag, name, species, breed, sex, birthDate, motherId, fatherId, weight, status, registeredAt, photoUrl, weanedOn); }
    public static AnimalResource from(Animal animal) { return new AnimalResource(animal.getId(), animal.getRancherId(), animal.getLotId(), animal.getEarTag(), animal.getName(), animal.getSpecies(), animal.getBreed(), animal.getSex(), animal.getBirthDate(), animal.getMotherId(), animal.getFatherId(), animal.getWeight(), animal.getStatus(), animal.getRegisteredAt(), animal.getPhotoUrl(), animal.getWeanedOn()); }
}

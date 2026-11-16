
package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.interfaces.acl.AnimalRecord;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AppointmentAnimalDetailResource(
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
    public static AppointmentAnimalDetailResource from(AnimalRecord animal) {
        return new AppointmentAnimalDetailResource(
                animal.id(),
                animal.rancherId(),
                animal.lotId(),
                animal.earTag(),
                animal.name(),
                animal.species(),
                animal.breed(),
                animal.sex(),
                animal.birthDate(),
                animal.motherId(),
                animal.fatherId(),
                animal.weight(),
                animal.status(),
                animal.registeredAt(),
                animal.photoUrl(),
                animal.weanedOn()
        );
    }
}

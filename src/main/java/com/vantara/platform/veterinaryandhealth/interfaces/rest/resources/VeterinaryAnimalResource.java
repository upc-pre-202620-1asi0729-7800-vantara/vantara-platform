
package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.interfaces.acl.AnimalRecord;

import java.math.BigDecimal;

public record VeterinaryAnimalResource(
        String appointmentId,
        String id,
        String rancherId,
        String lotId,
        String earTag,
        String name,
        String breed,
        String sex,
        BigDecimal weight,
        String photoUrl
) {

    public static VeterinaryAnimalResource from(
            String appointmentId,
            AnimalRecord animal) {

        return new VeterinaryAnimalResource(
                appointmentId,
                animal.id(),
                animal.rancherId(),
                animal.lotId(),
                animal.earTag(),
                animal.name(),
                animal.breed(),
                animal.sex(),
                animal.weight(),
                animal.photoUrl()
        );
    }
}

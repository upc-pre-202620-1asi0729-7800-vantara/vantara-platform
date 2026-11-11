package com.vantara.platform.livestock.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ACL facade that exposes the livestock management capabilities other bounded contexts need.
 *
 * <p>This is the published contract of the livestock context: it uses only primitive and JDK types so consumers never
 * depend on livestock internals. The implementation belongs to the livestock context and lives in its application
 * layer ({@code livestock.application.acl}).</p>
 */
public interface LivestockContextFacade {

    /**
     * @param animalId identifier of the animal
     * @return {@code true} when the animal exists
     */
    boolean existsAnimal(Long animalId);

    /**
     * @param animalId identifier of the animal
     * @return {@code true} when the animal is active, female and not a calf, so it can have reproductive events
     */
    boolean isReproductiveFemale(Long animalId);

    /**
     * @param earTag ear tag to look up
     * @return {@code true} when an animal already uses the ear tag
     */
    boolean existsAnimalByEarTag(String earTag);

    /**
     * Registers a calf born to an animal; the calf inherits the lot, species and breed of its mother.
     *
     * @param motherId  identifier of the mother
     * @param name      name of the calf
     * @param earTag    ear tag of the calf
     * @param sex       {@code FEMALE} or {@code MALE}
     * @param weightKg  birth weight in kilograms
     * @param birthDate birth date
     * @return identifier of the registered calf, or {@code 0L} when it could not be registered
     */
    Long registerCalf(Long motherId, String name, String earTag, String sex, BigDecimal weightKg, LocalDate birthDate);

    /**
     * Marks a calf as weaned, updating its weight when one is given.
     *
     * @param calfId    identifier of the calf
     * @param motherId  identifier of the mother the calf must be linked to
     * @param weanedOn  weaning date
     * @param weightKg  weight at weaning in kilograms, or {@code null} to keep the current weight
     * @return {@code true} when the calf was updated; {@code false} when it is not linked to the mother, is inactive
     *         or was already weaned
     */
    boolean registerCalfWeaning(Long calfId, Long motherId, LocalDate weanedOn, BigDecimal weightKg);
}

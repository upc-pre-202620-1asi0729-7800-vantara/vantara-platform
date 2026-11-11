package com.vantara.platform.livestock.interfaces.acl;

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
}

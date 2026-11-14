package com.vantara.platform.livestockmanagement.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ACL facade that exposes the livestock capabilities other bounded contexts need.
 *
 * <p>This is the published contract of the livestock context. It uses only JDK types, so consumers
 * never depend on livestock internals. Animals are identified by their string (UUID) identifier,
 * the same one livestock publishes in its integration events. The implementation lives in
 * {@code livestockmanagement.application.acl}.</p>
 *
 * <p>Operations that write run inside the caller's transaction: if the caller fails afterwards,
 * livestock changes are rolled back with it.</p>
 */
public interface LivestockContextFacade {

    /**
     * @param animalId identifier of the animal
     * @return {@code true} when the animal exists
     */
    boolean existsAnimal(String animalId);

    /**
     * @param animalId identifier of the animal
     * @return {@code true} when the animal is active, female and not a calf (six months or older),
     *         so it can have reproductive events
     */
    boolean isReproductiveFemale(String animalId);

    /**
     * Checks an ear tag against the inventory of the rancher that owns the given animal, so the
     * caller does not need to know the rancher.
     *
     * @param animalId identifier of an animal of the rancher (for example the mother of a calf)
     * @param earTag   ear tag to look up
     * @return {@code true} when that rancher already has an animal with the ear tag; {@code false}
     *         when it does not, or when the animal does not exist
     */
    boolean existsEarTagInRancherOf(String animalId, String earTag);

    /**
     * Registers a calf born to an animal. The calf inherits rancher, lot, species and breed from
     * its mother and is registered as active.
     *
     * @param motherId  identifier of the mother
     * @param name      name of the calf
     * @param earTag    ear tag of the calf
     * @param sex       {@code FEMALE} or {@code MALE}
     * @param weightKg  birth weight in kilograms
     * @param birthDate birth date
     * @return the identifier of the calf, or empty when the mother does not exist or the ear tag
     *         is already taken
     * @throws IllegalArgumentException when the data violates a livestock rule (for example a
     *                                  future birth date)
     */
    Optional<String> registerCalf(
            String motherId,
            String name,
            String earTag,
            String sex,
            BigDecimal weightKg,
            LocalDate birthDate
    );

    /**
     * Marks a calf as weaned, updating its weight when one is given.
     *
     * @param calfId   identifier of the calf
     * @param motherId identifier of the mother the calf must be linked to
     * @param weanedOn weaning date
     * @param weightKg weight at weaning in kilograms, or {@code null} to keep the current weight
     * @return {@code true} when the calf was updated; {@code false} when it does not exist, is not
     *         linked to that mother, is inactive or was already weaned
     */
    boolean registerCalfWeaning(
            String calfId,
            String motherId,
            LocalDate weanedOn,
            BigDecimal weightKg
    );

    /**
     * Lists the animals owned by a rancher. Returns an empty list for a blank or unknown rancher.
     *
     * @param rancherId owner whose animals are listed
     * @return the animals of that rancher only
     */
    List<AnimalRecord> listAnimalsOf(String rancherId);

    /**
     * Lists the lots owned by a rancher. Returns an empty list for a blank or unknown rancher.
     *
     * @param rancherId owner whose lots are listed
     * @return the lots of that rancher only
     */
    List<LotRecord> listLotsOf(String rancherId);

    /**
     * Lists the feeding plans of the lots owned by a rancher.
     *
     * @param rancherId owner whose plans are listed
     * @return the plans of that rancher's lots only
     */
    List<FeedingPlanRecord> listFeedingPlansOf(String rancherId);

    /**
     * Lists the ingredients of the feeding plans of a rancher's lots.
     *
     * @param rancherId owner whose plan items are listed
     * @return the plan items of that rancher only
     */
    List<FeedingPlanItemRecord> listFeedingPlanItemsOf(String rancherId);

    /**
     * Lists the feeding records of the lots owned by a rancher.
     *
     * @param rancherId owner whose feeding records are listed
     * @return the feeding records of that rancher's lots only
     */
    List<FeedingLogRecord> listFeedingLogsOf(String rancherId);
}

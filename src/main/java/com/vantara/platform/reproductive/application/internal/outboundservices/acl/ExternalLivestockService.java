package com.vantara.platform.reproductive.application.internal.outboundservices.acl;

import com.vantara.platform.livestock.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.reproductive.domain.model.commands.CalfRegistration;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.model.valueobjects.WeightInKg;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * ACL service used by the Reproductive bounded context to interact with livestock management.
 *
 * <p>Translates reproductive value objects into the primitive contract of {@link LivestockContextFacade}.</p>
 */
@Service
public class ExternalLivestockService {

    private final LivestockContextFacade livestockContextFacade;

    public ExternalLivestockService(LivestockContextFacade livestockContextFacade) {
        this.livestockContextFacade = livestockContextFacade;
    }

    /**
     * @return {@code true} when the animal exists in livestock
     */
    public boolean existsAnimal(AnimalId animalId) {
        return livestockContextFacade.existsAnimal(animalId.animalId());
    }

    /**
     * @return {@code true} when the animal is an active, adult female
     */
    public boolean isReproductiveFemale(AnimalId animalId) {
        return livestockContextFacade.isReproductiveFemale(animalId.animalId());
    }

    /**
     * @return {@code true} when an animal already uses the ear tag
     */
    public boolean existsAnimalByEarTag(String earTag) {
        return livestockContextFacade.existsAnimalByEarTag(earTag);
    }

    /**
     * Registers a calf in livestock.
     *
     * @return the calf identifier, or empty when livestock could not register it
     */
    public Optional<AnimalId> registerCalf(AnimalId motherId, CalfRegistration calf, LocalDate birthDate) {
        var calfId = livestockContextFacade.registerCalf(
                motherId.animalId(), calf.name(), calf.earTag(), calf.sex().name(), calf.weightKg(), birthDate);
        return calfId == null || calfId == 0L ? Optional.empty() : Optional.of(new AnimalId(calfId));
    }

    /**
     * Marks a calf as weaned in livestock.
     *
     * @return {@code true} when livestock accepted the weaning
     */
    public boolean registerCalfWeaning(AnimalId calfId, AnimalId motherId, LocalDate weanedOn, WeightInKg weight) {
        return livestockContextFacade.registerCalfWeaning(
                calfId.animalId(), motherId.animalId(), weanedOn, weight == null ? null : weight.kilograms());
    }
}

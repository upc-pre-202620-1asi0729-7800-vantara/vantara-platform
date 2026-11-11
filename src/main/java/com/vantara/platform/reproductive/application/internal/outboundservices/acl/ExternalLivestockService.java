package com.vantara.platform.reproductive.application.internal.outboundservices.acl;

import com.vantara.platform.livestock.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import org.springframework.stereotype.Service;

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
}

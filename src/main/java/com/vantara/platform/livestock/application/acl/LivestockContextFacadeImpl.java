package com.vantara.platform.livestock.application.acl;

import com.vantara.platform.livestock.interfaces.acl.LivestockContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TEMPORARY permissive implementation of {@link LivestockContextFacade}.
 *
 * <p>The livestock bounded context does not exist yet, so this adapter keeps the application startable and lets the
 * reproductive context run end to end. It does not read or write any real animal: every animal "exists", is an
 * eligible female, no ear tag is taken, and registered calves get a made-up identifier. Its answers are NOT real.</p>
 */
@Service
@Slf4j
public class LivestockContextFacadeImpl implements LivestockContextFacade {

    private final AtomicLong fakeCalfIds = new AtomicLong();

    @Override
    public boolean existsAnimal(Long animalId) {
        log.warn("TEMPORARY livestock adapter: assuming animal {} exists", animalId);
        return true;
    }

    @Override
    public boolean isReproductiveFemale(Long animalId) {
        log.warn("TEMPORARY livestock adapter: assuming animal {} is an active adult female", animalId);
        return true;
    }

    @Override
    public boolean existsAnimalByEarTag(String earTag) {
        log.warn("TEMPORARY livestock adapter: assuming ear tag '{}' is free", earTag);
        return false;
    }

    @Override
    public Long registerCalf(Long motherId, String name, String earTag, String sex, BigDecimal weightKg, LocalDate birthDate) {
        var fakeId = fakeCalfIds.incrementAndGet();
        log.warn("TEMPORARY livestock adapter: calf '{}' of mother {} was NOT stored; returning fake id {}",
                earTag, motherId, fakeId);
        return fakeId;
    }

    @Override
    public boolean registerCalfWeaning(Long calfId, Long motherId, LocalDate weanedOn, BigDecimal weightKg) {
        log.warn("TEMPORARY livestock adapter: weaning of calf {} was NOT stored", calfId);
        return true;
    }
}

/*
 * NOTE FOR THE NEXT BOUNDED CONTEXT (livestock management):
 *
 * This class is a stopgap created with feature/reproductive. When you build the livestock context:
 *   1. DELETE this file and implement LivestockContextFacade for real in the same package
 *      (livestock.application.acl), backed by the livestock command and query services.
 *   2. Keep the published contract in livestock.interfaces.acl and honour its semantics:
 *        - isReproductiveFemale: animal is active, female and not a calf.
 *        - registerCalf: the calf inherits lot, species and breed from its mother; return 0L if it cannot be stored.
 *        - registerCalfWeaning: false when the calf is not linked to that mother, is inactive or is already weaned.
 *   3. registerCalf and registerCalfWeaning run inside the transaction of the reproductive command services, so
 *      implement them without opening an independent transaction (REQUIRES_NEW) and without swallowing exceptions,
 *      otherwise a failed calving could leave an orphan calf.
 *   4. Check that the real ear-tag rule (existsAnimalByEarTag) matches the conflict (409) reproductive returns.
 */

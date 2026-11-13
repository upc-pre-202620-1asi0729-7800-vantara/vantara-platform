package com.vantara.platform.livestockmanagement.application.acl;

import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * TEMPORARY permissive implementation of {@link LivestockContextFacade}.
 *
 * <p>The livestock bounded context is not part of this branch yet, so this adapter keeps the application
 * startable and lets the reproductive context run end to end. It does not read or write any real animal: every
 * animal "exists", is an eligible female, no ear tag is taken, and registered calves get a made-up identifier.
 * Its answers are NOT real.</p>
 *
 * <p>When {@code feature/livestock-management} is merged, DELETE this file: that branch ships the real
 * implementation at this same path, so Git will report a conflict here and the real one must win.</p>
 */
@Service
@Slf4j
public class LivestockContextFacadeImpl implements LivestockContextFacade {

    @Override
    public boolean existsAnimal(String animalId) {
        log.warn("TEMPORARY livestock adapter: assuming animal {} exists", animalId);
        return true;
    }

    @Override
    public boolean isReproductiveFemale(String animalId) {
        log.warn("TEMPORARY livestock adapter: assuming animal {} is an active adult female", animalId);
        return true;
    }

    @Override
    public boolean existsEarTagInRancherOf(String animalId, String earTag) {
        log.warn("TEMPORARY livestock adapter: assuming ear tag '{}' is free", earTag);
        return false;
    }

    @Override
    public Optional<String> registerCalf(String motherId, String name, String earTag, String sex,
                                         BigDecimal weightKg, LocalDate birthDate) {
        var fakeId = UUID.randomUUID().toString();
        log.warn("TEMPORARY livestock adapter: calf '{}' of mother {} was NOT stored; returning fake id {}",
                earTag, motherId, fakeId);
        return Optional.of(fakeId);
    }

    @Override
    public boolean registerCalfWeaning(String calfId, String motherId, LocalDate weanedOn, BigDecimal weightKg) {
        log.warn("TEMPORARY livestock adapter: weaning of calf {} was NOT stored", calfId);
        return true;
    }
}

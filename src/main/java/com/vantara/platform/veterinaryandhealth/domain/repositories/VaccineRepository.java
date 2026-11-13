package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.List;

/**
 * Domain repository contract for {@link Vaccine} aggregates.
 */
public interface VaccineRepository {

    Vaccine save(Vaccine vaccine);

    List<Vaccine> findAll();

    List<Vaccine> findAllByAnimalId(AnimalId animalId);
}

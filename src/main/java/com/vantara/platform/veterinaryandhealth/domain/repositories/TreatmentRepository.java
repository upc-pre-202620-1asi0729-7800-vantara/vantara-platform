package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

import java.util.List;

/**
 * Domain repository contract for {@link Treatment} aggregates.
 */
public interface TreatmentRepository {

    Treatment save(Treatment treatment);

    List<Treatment> findAll();

    List<Treatment> findAllByAnimalId(AnimalId animalId);
}

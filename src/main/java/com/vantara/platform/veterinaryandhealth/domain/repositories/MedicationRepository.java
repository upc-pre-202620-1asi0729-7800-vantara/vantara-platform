package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Medication;

import java.util.List;
import java.util.Optional;

/**
 * Read-only domain repository contract for the {@link Medication} catalog.
 */
public interface MedicationRepository {

    Optional<Medication> findById(String id);

    List<Medication> findAll();
}

package com.vantara.platform.veterinaryandhealth.domain.model.queries;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

/**
 * Query to list vaccine doses, optionally filtered by animal.
 *
 * @param animalId filter by animal identifier, or null
 */
public record GetAllVaccinesQuery(AnimalId animalId) {
}

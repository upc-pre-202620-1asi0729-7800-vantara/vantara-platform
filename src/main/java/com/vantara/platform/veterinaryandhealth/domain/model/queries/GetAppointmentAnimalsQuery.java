package com.vantara.platform.veterinaryandhealth.domain.model.queries;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

/**
 * Query to list appointment-animal links, optionally filtered by appointment or animal.
 *
 * @param appointmentId filter by appointment identifier, or null
 * @param animalId      filter by animal identifier, or null
 */
public record GetAppointmentAnimalsQuery(String appointmentId, AnimalId animalId) {
}

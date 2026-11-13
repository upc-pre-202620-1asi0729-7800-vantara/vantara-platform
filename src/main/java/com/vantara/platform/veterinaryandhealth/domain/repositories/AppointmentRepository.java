package com.vantara.platform.veterinaryandhealth.domain.repositories;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for {@link Appointment} aggregates.
 */
public interface AppointmentRepository {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(String id);

    List<Appointment> findAll();
}

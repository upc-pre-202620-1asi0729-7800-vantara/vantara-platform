package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentRepository;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.assemblers.AppointmentPersistenceAssembler;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.AppointmentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the {@link AppointmentRepository} domain port with Spring Data JPA.
 */
@Repository
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentPersistenceRepository persistenceRepository;

    public AppointmentRepositoryImpl(AppointmentPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        var entity = persistenceRepository.save(AppointmentPersistenceAssembler.toPersistenceFromDomain(appointment));
        return AppointmentPersistenceAssembler.toDomainFromPersistence(entity);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return persistenceRepository.findById(id).map(AppointmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Appointment> findAll() {
        return persistenceRepository.findAll().stream()
                .map(AppointmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

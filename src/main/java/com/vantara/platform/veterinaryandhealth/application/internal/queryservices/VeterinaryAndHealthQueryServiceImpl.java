package com.vantara.platform.veterinaryandhealth.application.internal.queryservices;

import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Appointment;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.AppointmentAnimal;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Medication;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.MedicalRecord;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Treatment;
import com.vantara.platform.veterinaryandhealth.domain.model.aggregates.Vaccine;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllAppointmentsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllMedicalRecordsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllMedicationsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllTreatmentsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllVaccinesQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAppointmentAnimalsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAppointmentByIdQuery;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentAnimalRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.AppointmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicalRecordRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.MedicationRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.TreatmentRepository;
import com.vantara.platform.veterinaryandhealth.domain.repositories.VaccineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VeterinaryAndHealthQueryServiceImpl implements VeterinaryAndHealthQueryService {

    private final AppointmentRepository appointments;
    private final AppointmentAnimalRepository appointmentAnimals;
    private final MedicalRecordRepository medicalRecords;
    private final MedicationRepository medications;
    private final TreatmentRepository treatments;
    private final VaccineRepository vaccines;

    public VeterinaryAndHealthQueryServiceImpl(
            AppointmentRepository appointments,
            AppointmentAnimalRepository appointmentAnimals,
            MedicalRecordRepository medicalRecords,
            MedicationRepository medications,
            TreatmentRepository treatments,
            VaccineRepository vaccines) {
        this.appointments = appointments;
        this.appointmentAnimals = appointmentAnimals;
        this.medicalRecords = medicalRecords;
        this.medications = medications;
        this.treatments = treatments;
        this.vaccines = vaccines;
    }

    @Override
    public List<Appointment> handle(GetAllAppointmentsQuery query) {
        return appointments.findAll();
    }

    @Override
    public Optional<Appointment> handle(GetAppointmentByIdQuery query) {
        return appointments.findById(query.id());
    }

    @Override
    public List<AppointmentAnimal> handle(GetAppointmentAnimalsQuery query) {
        if (query.appointmentId() != null) {
            return appointmentAnimals.findAllByAppointmentId(query.appointmentId()).stream()
                    .filter(link -> query.animalId() == null || link.getAnimalId().equals(query.animalId()))
                    .toList();
        }
        if (query.animalId() != null) {
            return appointmentAnimals.findAllByAnimalId(query.animalId());
        }
        return appointmentAnimals.findAll();
    }

    @Override
    public List<MedicalRecord> handle(GetAllMedicalRecordsQuery query) {
        return query.animalId() == null
                ? medicalRecords.findAll()
                : medicalRecords.findAllByAnimalId(query.animalId());
    }

    @Override
    public List<Treatment> handle(GetAllTreatmentsQuery query) {
        return query.animalId() == null
                ? treatments.findAll()
                : treatments.findAllByAnimalId(query.animalId());
    }

    @Override
    public List<Medication> handle(GetAllMedicationsQuery query) {
        return medications.findAll();
    }

    @Override
    public List<Vaccine> handle(GetAllVaccinesQuery query) {
        return query.animalId() == null
                ? vaccines.findAll()
                : vaccines.findAllByAnimalId(query.animalId());
    }
}

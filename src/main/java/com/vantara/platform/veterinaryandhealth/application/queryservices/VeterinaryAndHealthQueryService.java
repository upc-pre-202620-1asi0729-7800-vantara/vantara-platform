package com.vantara.platform.veterinaryandhealth.application.queryservices;

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

import java.util.List;
import java.util.Optional;

/**
 * Read-side contract of the veterinary and health use cases.
 */
public interface VeterinaryAndHealthQueryService {

    List<Appointment> handle(GetAllAppointmentsQuery query);

    Optional<Appointment> handle(GetAppointmentByIdQuery query);

    List<AppointmentAnimal> handle(GetAppointmentAnimalsQuery query);

    List<MedicalRecord> handle(GetAllMedicalRecordsQuery query);

    List<Treatment> handle(GetAllTreatmentsQuery query);

    List<Medication> handle(GetAllMedicationsQuery query);

    List<Vaccine> handle(GetAllVaccinesQuery query);
}

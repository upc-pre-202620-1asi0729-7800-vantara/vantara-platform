package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllAppointmentsQuery;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAppointmentByIdQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.CreateAppointmentResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.UpdateAppointmentResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.ApplicationErrorFromCommandFailureAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.AppointmentResourceFromEntityAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.CreateAppointmentCommandFromResourceAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.UpdateAppointmentCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api/v1/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments", description = "Veterinary appointments between ranchers and veterinarians")
public class AppointmentsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;

    public AppointmentsController(VeterinaryAndHealthCommandService commandService,
                                  VeterinaryAndHealthQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the appointments")
    public ResponseEntity<List<AppointmentResource>> getAllAppointments() {
        var appointments = queryService.handle(new GetAllAppointmentsQuery()).stream()
                .map(AppointmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment by id")
    public ResponseEntity<?> getAppointmentById(@PathVariable String id) {
        var appointment = queryService.handle(new GetAppointmentByIdQuery(id));
        if (appointment.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("appointment", id));
        }
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment.orElseThrow()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Schedule an appointment")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentResource resource) {
        var result = commandService.handle(CreateAppointmentCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                AppointmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an appointment status, reason or schedule")
    public ResponseEntity<?> updateAppointment(@PathVariable String id,
                                               @RequestBody UpdateAppointmentResource resource) {
        var result = commandService.handle(UpdateAppointmentCommandFromResourceAssembler.toCommandFromResource(id, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                AppointmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}

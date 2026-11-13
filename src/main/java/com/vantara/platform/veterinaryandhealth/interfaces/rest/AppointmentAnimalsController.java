package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAppointmentAnimalsQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentAnimalResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.AppointmentAnimalResourceFromEntityAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.ApplicationErrorFromCommandFailureAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.LinkAnimalToAppointmentCommandFromResourceAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.QueryParamAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api/v1/appointmentAnimals", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointment animals", description = "Animals attached to veterinary appointments")
public class AppointmentAnimalsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;

    public AppointmentAnimalsController(VeterinaryAndHealthCommandService commandService,
                                        VeterinaryAndHealthQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the appointment-animal links, optionally filtered by appointment or animal")
    public ResponseEntity<List<AppointmentAnimalResource>> getAppointmentAnimals(
            @RequestParam(required = false) String appointmentId,
            @RequestParam(required = false) String animalId) {
        var links = queryService.handle(new GetAppointmentAnimalsQuery(
                QueryParamAssembler.toTextOrNull(appointmentId),
                QueryParamAssembler.toAnimalIdOrNull(animalId))).stream()
                .map(AppointmentAnimalResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(links);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Attach an animal to an appointment")
    public ResponseEntity<?> linkAnimal(@Valid @RequestBody AppointmentAnimalResource resource) {
        var result = commandService.handle(LinkAnimalToAppointmentCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                AppointmentAnimalResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}

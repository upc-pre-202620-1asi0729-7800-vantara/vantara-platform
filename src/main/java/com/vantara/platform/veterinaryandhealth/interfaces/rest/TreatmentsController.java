package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllTreatmentsQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.PrescribeTreatmentResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.TreatmentResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.ApplicationErrorFromCommandFailureAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.PrescribeTreatmentCommandFromResourceAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.QueryParamAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.TreatmentResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/treatments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Treatments", description = "Medical treatments prescribed within clinical records")
public class TreatmentsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;

    public TreatmentsController(VeterinaryAndHealthCommandService commandService,
                                VeterinaryAndHealthQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the treatments, optionally filtered by animal")
    public ResponseEntity<List<TreatmentResource>> getTreatments(@RequestParam(required = false) String animalId) {
        var treatments = queryService.handle(new GetAllTreatmentsQuery(QueryParamAssembler.toAnimalIdOrNull(animalId)))
                .stream()
                .map(TreatmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(treatments);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Prescribe a treatment")
    public ResponseEntity<?> prescribeTreatment(@Valid @RequestBody PrescribeTreatmentResource resource) {
        var result = commandService.handle(PrescribeTreatmentCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                TreatmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}

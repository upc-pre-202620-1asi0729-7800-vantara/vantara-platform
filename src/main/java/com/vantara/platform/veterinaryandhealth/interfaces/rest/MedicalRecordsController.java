package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllMedicalRecordsQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.MedicalRecordResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.RegisterMedicalRecordResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.ApplicationErrorFromCommandFailureAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.MedicalRecordResourceFromEntityAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.QueryParamAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.RegisterMedicalRecordCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/medicalRecords", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medical records", description = "Clinical records issued by veterinarians")
public class MedicalRecordsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;

    public MedicalRecordsController(VeterinaryAndHealthCommandService commandService,
                                    VeterinaryAndHealthQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the clinical records, optionally filtered by animal")
    public ResponseEntity<List<MedicalRecordResource>> getMedicalRecords(
            @RequestParam(required = false) String animalId) {
        var records = queryService.handle(new GetAllMedicalRecordsQuery(QueryParamAssembler.toAnimalIdOrNull(animalId)))
                .stream()
                .map(MedicalRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(records);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a clinical record")
    public ResponseEntity<?> registerMedicalRecord(@Valid @RequestBody RegisterMedicalRecordResource resource) {
        var result = commandService.handle(RegisterMedicalRecordCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                MedicalRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}

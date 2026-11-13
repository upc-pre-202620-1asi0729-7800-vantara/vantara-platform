package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllVaccinesQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.RegisterVaccineResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VaccineResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.ApplicationErrorFromCommandFailureAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.QueryParamAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.RegisterVaccineCommandFromResourceAssembler;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.VaccineResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vaccines", description = "Vaccine doses applied to animals")
public class VaccinesController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;

    public VaccinesController(VeterinaryAndHealthCommandService commandService,
                              VeterinaryAndHealthQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the vaccine doses, optionally filtered by animal")
    public ResponseEntity<List<VaccineResource>> getVaccines(@RequestParam(required = false) String animalId) {
        var vaccines = queryService.handle(new GetAllVaccinesQuery(QueryParamAssembler.toAnimalIdOrNull(animalId)))
                .stream()
                .map(VaccineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vaccines);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a vaccine dose")
    public ResponseEntity<?> registerVaccine(@Valid @RequestBody RegisterVaccineResource resource) {
        var result = commandService.handle(RegisterVaccineCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                VaccineResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}

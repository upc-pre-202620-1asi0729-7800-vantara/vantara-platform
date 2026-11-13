package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllMedicationsQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.MedicationResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.transform.MedicationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api/v1/medications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medications", description = "Veterinary medication catalog")
public class MedicationsController {

    private final VeterinaryAndHealthQueryService queryService;

    public MedicationsController(VeterinaryAndHealthQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the medication catalog")
    public ResponseEntity<List<MedicationResource>> getMedications() {
        var medications = queryService.handle(new GetAllMedicationsQuery()).stream()
                .map(MedicationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(medications);
    }
}

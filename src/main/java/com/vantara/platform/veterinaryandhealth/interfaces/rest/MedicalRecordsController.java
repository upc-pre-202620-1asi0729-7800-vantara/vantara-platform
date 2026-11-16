
package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.internal.authorization.VeterinaryAuthorizationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(
        value = "/api/v1/medicalRecords",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Medical records",
        description = "Clinical records issued by veterinarians"
)
public class MedicalRecordsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;
    private final AuthenticatedUserIdentity identity;
    private final VeterinaryAuthorizationService authorization;

    public MedicalRecordsController(
            VeterinaryAndHealthCommandService commandService,
            VeterinaryAndHealthQueryService queryService,
            AuthenticatedUserIdentity identity,
            VeterinaryAuthorizationService authorization) {

        this.commandService = commandService;
        this.queryService = queryService;
        this.identity = identity;
        this.authorization = authorization;
    }

    @GetMapping
    @Operation(summary = "List the clinical records, optionally filtered by animal")
    public ResponseEntity<List<MedicalRecordResource>> getMedicalRecords(
            @RequestParam(required = false) String animalId,
            Principal principal) {

        var animalFilter = QueryParamAssembler.toAnimalIdOrNull(animalId);

        // If an animal was specified, validate access before querying.
        if (animalFilter != null) {
            authorization.requireAnimalAccess(principal, animalId);
        }

        var records = queryService.handle(
                        new GetAllMedicalRecordsQuery(animalFilter))
                .stream()
                // When listing all records, return only those the user can access.
                .filter(record -> authorization.canAccessAnimal(
                        principal,
                        record.getAnimalId().value()))
                .map(MedicalRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(records);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a clinical record")
    public ResponseEntity<?> registerMedicalRecord(
            @Valid @RequestBody RegisterMedicalRecordResource resource,
            Principal principal) {

        Long actorId = identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        var command =
                RegisterMedicalRecordCommandFromResourceAssembler
                        .toCommandFromResource(resource, actorId);

        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(
                        ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                MedicalRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }
}

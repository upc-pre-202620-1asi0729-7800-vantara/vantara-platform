
package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.internal.authorization.VeterinaryAuthorizationService;
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
        value = "/api/v1/treatments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Treatments",
        description = "Medical treatments prescribed within clinical records"
)
public class TreatmentsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;
    private final AuthenticatedUserIdentity identity;
    private final VeterinaryAuthorizationService authorization;

    public TreatmentsController(
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
    @Operation(summary = "List the treatments, optionally filtered by animal")
    public ResponseEntity<List<TreatmentResource>> getTreatments(
            @RequestParam(required = false) String animalId,
            Principal principal) {

        var animalFilter = QueryParamAssembler.toAnimalIdOrNull(animalId);

        if (animalFilter != null) {
            authorization.requireAnimalAccess(principal, animalId);
        }

        var treatments = queryService.handle(
                        new GetAllTreatmentsQuery(animalFilter))
                .stream()
                .filter(treatment -> authorization.canAccessAnimal(
                        principal,
                        treatment.getAnimalId().value()))
                .map(TreatmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(treatments);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Prescribe a treatment")
    public ResponseEntity<?> prescribeTreatment(
            @Valid @RequestBody PrescribeTreatmentResource resource,
            Principal principal) {

        Long actorId = identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        var command =
                PrescribeTreatmentCommandFromResourceAssembler
                        .toCommandFromResource(resource, actorId);

        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(
                        ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                TreatmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }
}

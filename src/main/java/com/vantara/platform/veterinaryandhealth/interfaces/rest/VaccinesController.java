
package com.vantara.platform.veterinaryandhealth.interfaces.rest;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VeterinaryAnimalResource;
import java.util.List;
import java.security.Principal;
import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.internal.authorization.VeterinaryAuthorizationService;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinaryAndHealthQueryService;
import com.vantara.platform.veterinaryandhealth.domain.model.queries.GetAllVaccinesQuery;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.RegisterVaccineResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VaccineResource;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VeterinaryAnimalResource;
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
        value = "/api/v1/vaccines",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Vaccines",
        description = "Vaccine doses applied to animals"
)
public class VaccinesController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;
    private final AuthenticatedUserIdentity identity;
    private final VeterinaryAuthorizationService authorization;

    public VaccinesController(
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
    @Operation(summary = "List the vaccine doses, optionally filtered by animal")
    public ResponseEntity<List<VaccineResource>> getVaccines(
            @RequestParam(required = false) String animalId,
            Principal principal) {

        var animalFilter = QueryParamAssembler.toAnimalIdOrNull(animalId);

        if (animalFilter != null) {
            authorization.requireAnimalAccess(principal, animalId);
        }

        var vaccines = queryService.handle(
                        new GetAllVaccinesQuery(animalFilter))
                .stream()
                .filter(vaccine -> authorization.canAccessAnimal(
                        principal,
                        vaccine.getAnimalId().value()))
                .map(VaccineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/accessible-animals")
    @Operation(summary = "List animals accessible to the authenticated veterinarian")
    public ResponseEntity<List<VeterinaryAnimalResource>> getAccessibleAnimals(
            Principal principal) {

        var animals = authorization
                .findAnimalsAccessibleToVeterinarian(principal)
                .stream()
                .map(access -> VeterinaryAnimalResource.from(
                        access.appointmentId(),
                        access.animal()
                ))
                .toList();

        return ResponseEntity.ok(animals);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a vaccine dose")
    public ResponseEntity<?> registerVaccine(
            @Valid @RequestBody RegisterVaccineResource resource,
            Principal principal) {

        Long actorId = identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        var command =
                RegisterVaccineCommandFromResourceAssembler
                        .toCommandFromResource(resource, actorId);

        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(
                        ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                VaccineResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }
}

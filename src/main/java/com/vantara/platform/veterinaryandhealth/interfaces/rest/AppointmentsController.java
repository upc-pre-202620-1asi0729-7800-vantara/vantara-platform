
package com.vantara.platform.veterinaryandhealth.interfaces.rest;
import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.AppointmentAnimalDetailResource;
import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.vantara.platform.veterinaryandhealth.application.commandservices.VeterinaryAndHealthCommandService;
import com.vantara.platform.veterinaryandhealth.application.internal.authorization.VeterinaryAuthorizationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api/v1/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments", description = "Veterinary appointments between ranchers and veterinarians")
public class AppointmentsController {

    private final VeterinaryAndHealthCommandService commandService;
    private final VeterinaryAndHealthQueryService queryService;
    private final VeterinaryAuthorizationService authorization;
    private final AuthenticatedUserIdentity identity;
    private final IamContextFacade iam;

    private final LivestockContextFacade livestock;
    public AppointmentsController(
            VeterinaryAndHealthCommandService commandService,
            VeterinaryAndHealthQueryService queryService,
            VeterinaryAuthorizationService authorization,
            AuthenticatedUserIdentity identity,
            IamContextFacade iam,
            LivestockContextFacade livestock) {

        this.commandService = commandService;
        this.queryService = queryService;
        this.authorization = authorization;
        this.identity = identity;
        this.iam = iam;
        this.livestock = livestock;
    }

    @GetMapping
    @Operation(summary = "List appointments accessible to the authenticated user")
    public ResponseEntity<List<AppointmentResource>> getAllAppointments(Principal principal) {
        var appointments = queryService.handle(new GetAllAppointmentsQuery())
                .stream()
                .filter(appointment -> authorization.canAccessAppointment(principal, appointment))
                .map(AppointmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an accessible appointment by id")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable String id,
            Principal principal) {

        var appointment = queryService.handle(new GetAppointmentByIdQuery(id));

        if (appointment.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("appointment", id));
        }

        var appointmentEntity = appointment.orElseThrow();

        authorization.requireAppointmentAccess(principal, appointmentEntity);

        return ResponseEntity.ok(
                AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointmentEntity));
    }

    @GetMapping("/{appointmentId}/animals/{animalId}")
    @Operation(summary = "Get an animal linked to an accessible appointment")
    public ResponseEntity<?> getAppointmentAnimal(
            @PathVariable String appointmentId,
            @PathVariable String animalId,
            Principal principal) {

        // Verify that the authenticated user can access this animal
        // through the specified appointment.
        authorization.requireAnimalAccessThroughAppointment(
                principal,
                appointmentId,
                animalId
        );

        // Retrieve the animal through the Livestock bounded context ACL.
        var animal = livestock.findAnimalById(animalId);

        if (animal.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("animal", animalId)
            );
        }

        return ResponseEntity.ok(
                AppointmentAnimalDetailResource.from(animal.get())
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Schedule an appointment")
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody CreateAppointmentResource resource,
            Principal principal) {

        // Obtain the authenticated user's ID from the security context.
        Long authenticatedUserId = identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user could not be identified"));

        boolean isRancher = identity.isRancher(principal);
        boolean isVeterinarian = identity.isVeterinarian(principal);

        // Reject users without a supported role or with ambiguous roles.
        if (isRancher == isVeterinarian) {
            throw new AccessDeniedException(
                    "Only ranchers and veterinarians can create appointments");
        }

        Long rancherId;
        Long veterinarianId;

        if (isRancher) {
            // A rancher can only create an appointment for themselves.
            rancherId = authenticatedUserId;
            veterinarianId = requireTargetUserWithRole(
                    resource.veterinarianId(),
                    "ROLE_VETERINARIAN");
        } else {
            // A veterinarian creates an appointment on behalf of a rancher.
            rancherId = requireTargetUserWithRole(
                    resource.rancherId(),
                    "ROLE_RANCHER");
            veterinarianId = authenticatedUserId;
        }

        // requestedBy always comes from the authenticated user, never the request body.
        var command = CreateAppointmentCommandFromResourceAssembler.toCommandFromResource(
                resource,
                rancherId,
                veterinarianId,
                authenticatedUserId
        );

        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                AppointmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    private Long requireTargetUserWithRole(Long targetUserId, String requiredRole) {
        if (targetUserId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected user ID is required");
        }

        Set<String> roles = iam.fetchRoleNamesByUserId(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The selected user does not exist"));

        if (!roles.contains(requiredRole)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected user does not have the required role");
        }

        return targetUserId;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an appointment status, reason or schedule")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String id,
            @Valid @RequestBody UpdateAppointmentResource resource,
            Principal principal) {

        // Obtain the authenticated user's ID.
        Long authenticatedUserId = identity.userId(principal)
                .map(Long::valueOf)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user could not be identified"));

        // Pass the authenticated identity to the command.
        var command = UpdateAppointmentCommandFromResourceAssembler.toCommandFromResource(
                id,
                resource,
                authenticatedUserId
        );

        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result.mapError(ApplicationErrorFromCommandFailureAssembler::toApplicationError),
                AppointmentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}

package com.vantara.platform.reproductive.interfaces.rest;

import com.vantara.platform.reproductive.application.commandservices.ReproductiveRecordCommandService;
import com.vantara.platform.reproductive.application.queryservices.ReproductiveRecordQueryService;
import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordByIdQuery;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterPregnancyResource;
import com.vantara.platform.reproductive.interfaces.rest.resources.ReproductiveRecordResource;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterCalvingResource;
import com.vantara.platform.reproductive.interfaces.rest.transform.RegisterCalvingCommandFromResourceAssembler;
import com.vantara.platform.reproductive.interfaces.rest.transform.RegisterPregnancyCommandFromResourceAssembler;
import com.vantara.platform.reproductive.interfaces.rest.transform.ReproductiveRecordResourceFromEntityAssembler;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import com.vantara.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes reproductive records and the reproductive events registered on them.
 */
@RestController
@RequestMapping(value = "/api/v1/reproductive-records", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Reproductive Records", description = "Reproductive management endpoints")
public class ReproductiveRecordsController {

    private final ReproductiveRecordCommandService commandService;
    private final ReproductiveRecordQueryService queryService;

    public ReproductiveRecordsController(
            ReproductiveRecordCommandService commandService,
            ReproductiveRecordQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Registers the pregnancy of an animal.
     *
     * @param resource the {@link RegisterPregnancyResource} with the pregnancy data
     * @return the created {@link ReproductiveRecordResource}
     */
    @PostMapping
    @Operation(
            summary = "Register pregnancy",
            description = "Confirms the pregnancy of an active adult female and opens its reproductive record."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pregnancy registered successfully",
                    content = @Content(schema = @Schema(implementation = ReproductiveRecordResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or missing data, or date out of range"),
            @ApiResponse(responseCode = "404", description = "Animal not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - the animal already has a pregnancy in progress"),
            @ApiResponse(responseCode = "422", description = "The animal is not an active adult female")
    })
    public ResponseEntity<?> registerPregnancy(@RequestBody RegisterPregnancyResource resource) {
        var command = RegisterPregnancyCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command).flatMap(this::findRecord);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ReproductiveRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    /**
     * Registers the calving that ends a pregnancy, optionally registering the calf.
     *
     * @param recordId the reproductive record identifier
     * @param resource the {@link RegisterCalvingResource} with the calving data
     * @return the updated {@link ReproductiveRecordResource}
     */
    @PostMapping("/{recordId}/calvings")
    @Operation(
            summary = "Register calving",
            description = "Registers the calving that ends a pregnancy. When calf data is sent, the calf is registered in livestock."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Calving registered successfully",
                    content = @Content(schema = @Schema(implementation = ReproductiveRecordResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or missing data, or date out of range"),
            @ApiResponse(responseCode = "404", description = "Reproductive record not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - the calf ear tag already belongs to another animal"),
            @ApiResponse(responseCode = "422", description = "The pregnancy already ended or the mother is not an active adult female")
    })
    public ResponseEntity<?> registerCalving(
            @PathVariable
            @Parameter(description = "Reproductive record identifier", example = "1", required = true)
            Long recordId,

            @RequestBody RegisterCalvingResource resource
    ) {
        var command = RegisterCalvingCommandFromResourceAssembler.toCommandFromResource(recordId, resource);
        var result = commandService.handle(command).flatMap(this::findRecord);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ReproductiveRecordResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    private Result<ReproductiveRecord, ApplicationError> findRecord(Long recordId) {
        return queryService.handle(new GetReproductiveRecordByIdQuery(recordId))
                .<Result<ReproductiveRecord, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("ReproductiveRecord", recordId.toString())));
    }
}

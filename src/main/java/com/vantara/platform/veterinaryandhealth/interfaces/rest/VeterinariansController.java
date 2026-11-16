
package com.vantara.platform.veterinaryandhealth.interfaces.rest;

import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinarianQueryService;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VeterinarianResource;
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
@RequestMapping(
        value = "/api/v1/veterinarians",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(name = "Veterinarians", description = "Available veterinarians")
public class VeterinariansController {

    private final VeterinarianQueryService veterinarianQueryService;

    public VeterinariansController(VeterinarianQueryService veterinarianQueryService) {
        this.veterinarianQueryService = veterinarianQueryService;
    }

    @GetMapping
    @Operation(summary = "List available veterinarians")
    public ResponseEntity<List<VeterinarianResource>> getAllVeterinarians() {
        return ResponseEntity.ok(veterinarianQueryService.getAllVeterinarians());
    }
}

package com.vantara.platform.reports.interfaces.rest;

import com.vantara.platform.reports.application.queryservices.ReportQueryService;
import com.vantara.platform.reports.domain.exceptions.ReportSourceUnavailableException;
import com.vantara.platform.reports.domain.model.queries.GetFeedingReportQuery;
import com.vantara.platform.reports.domain.model.queries.GetLivestockReportQuery;
import com.vantara.platform.reports.interfaces.rest.resources.FeedingReportResource;
import com.vantara.platform.reports.interfaces.rest.resources.LivestockReportResource;
import com.vantara.platform.reports.interfaces.rest.transform.FeedingReportResourceFromEntityAssembler;
import com.vantara.platform.reports.interfaces.rest.transform.LivestockReportResourceFromEntityAssembler;
import com.vantara.platform.reports.interfaces.rest.transform.ReportFilterAssembler;
import com.vantara.platform.shared.interfaces.rest.resources.ErrorResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;

/**
 * Livestock and feeding reports of the authenticated rancher. The rancher is resolved from the bearer token.
 */
@RestController
@RequestMapping(value = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Livestock and feeding reports of the authenticated rancher")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    private final ReportQueryService reportQueryService;
    private final AuthenticatedRancher authenticatedRancher;

    public ReportsController(ReportQueryService reportQueryService, AuthenticatedRancher authenticatedRancher) {
        this.reportQueryService = reportQueryService;
        this.authenticatedRancher = authenticatedRancher;
    }

    @GetMapping("/livestock")
    @Operation(summary = "Get the livestock report of the authenticated rancher")
    public ResponseEntity<?> getLivestockReport(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startsOn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endsOn,
            @RequestParam(required = false) String lotId,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String status) {
        var rancherId = authenticatedRancher.resolve(principal);
        if (rancherId.isEmpty()) {
            return unauthorized();
        }
        var filter = ReportFilterAssembler.toLivestockFilter(startsOn, endsOn, lotId, breed, sex, status);
        var report = reportQueryService.handle(new GetLivestockReportQuery(rancherId.orElseThrow(), filter));
        return ResponseEntity.ok(LivestockReportResourceFromEntityAssembler.toResourceFromEntity(report));
    }

    @GetMapping("/feeding")
    @Operation(summary = "Get the feeding report of the authenticated rancher")
    public ResponseEntity<?> getFeedingReport(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startsOn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endsOn,
            @RequestParam(required = false) String lotId) {
        var rancherId = authenticatedRancher.resolve(principal);
        if (rancherId.isEmpty()) {
            return unauthorized();
        }
        var filter = ReportFilterAssembler.toFeedingFilter(startsOn, endsOn, lotId);
        var report = reportQueryService.handle(new GetFeedingReportQuery(rancherId.orElseThrow(), filter));
        return ResponseEntity.ok(FeedingReportResourceFromEntityAssembler.toResourceFromEntity(report));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResource> handleInvalidParameter(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResource("VALIDATION_ERROR", "Parameter '" + ex.getName() + "' is invalid"));
    }

    @ExceptionHandler(ReportSourceUnavailableException.class)
    public ResponseEntity<ErrorResource> handleSourceUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResource("REPORT_SOURCE_UNAVAILABLE", "Report data is not available right now"));
    }

    private static ResponseEntity<ErrorResource> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResource("UNAUTHORIZED", "Authentication is required"));
    }
}

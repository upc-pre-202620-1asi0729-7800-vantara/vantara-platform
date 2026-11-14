package com.vantara.platform.reports.domain.model.valueobjects;

import java.util.Objects;

/**
 * Filters of the livestock report. Blank text filters are treated as absent.
 *
 * @param period period on which the animals were registered
 * @param lotId  lot to report on, or null for every lot
 * @param breed  exact breed, or null for every breed
 * @param sex    sex to report on (case-insensitive), or null for every sex
 * @param status exact animal status, or null for every status
 */
public record LivestockReportFilter(ReportPeriod period, String lotId, String breed, String sex, String status) {

    public LivestockReportFilter {
        Objects.requireNonNull(period, "Report period cannot be null");
        lotId = blankToNull(lotId);
        breed = blankToNull(breed);
        sex = blankToNull(sex);
        status = blankToNull(status);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

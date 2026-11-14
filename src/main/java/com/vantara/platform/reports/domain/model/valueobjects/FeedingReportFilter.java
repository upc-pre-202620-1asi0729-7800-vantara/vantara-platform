package com.vantara.platform.reports.domain.model.valueobjects;

import java.util.Objects;

/**
 * Filters of the feeding report. Blank text filters are treated as absent.
 *
 * @param period period of the feeding plans and records
 * @param lotId  lot to report on, or null for every lot
 */
public record FeedingReportFilter(ReportPeriod period, String lotId) {

    public FeedingReportFilter {
        Objects.requireNonNull(period, "Report period cannot be null");
        lotId = lotId == null || lotId.isBlank() ? null : lotId.trim();
    }
}

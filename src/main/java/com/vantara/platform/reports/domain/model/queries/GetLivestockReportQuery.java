package com.vantara.platform.reports.domain.model.queries;

import com.vantara.platform.reports.domain.model.valueobjects.LivestockReportFilter;

import java.util.Objects;

/**
 * Query for the livestock report of one rancher.
 *
 * @param rancherId owner whose data is reported, resolved from the authenticated account
 * @param filter    filters of the report
 */
public record GetLivestockReportQuery(String rancherId, LivestockReportFilter filter) {

    public GetLivestockReportQuery {
        if (rancherId == null || rancherId.isBlank()) {
            throw new IllegalArgumentException("Rancher id cannot be blank");
        }
        Objects.requireNonNull(filter, "Filter cannot be null");
    }
}

package com.vantara.platform.reports.domain.model.queries;

import com.vantara.platform.reports.domain.model.valueobjects.FeedingReportFilter;

import java.util.Objects;

/**
 * Query for the feeding report of one rancher.
 *
 * @param rancherId owner whose data is reported, resolved from the authenticated account
 * @param filter    filters of the report
 */
public record GetFeedingReportQuery(String rancherId, FeedingReportFilter filter) {

    public GetFeedingReportQuery {
        if (rancherId == null || rancherId.isBlank()) {
            throw new IllegalArgumentException("Rancher id cannot be blank");
        }
        Objects.requireNonNull(filter, "Filter cannot be null");
    }
}

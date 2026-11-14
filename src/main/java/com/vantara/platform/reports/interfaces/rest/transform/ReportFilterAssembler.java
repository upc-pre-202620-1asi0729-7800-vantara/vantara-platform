package com.vantara.platform.reports.interfaces.rest.transform;

import com.vantara.platform.reports.domain.model.valueobjects.FeedingReportFilter;
import com.vantara.platform.reports.domain.model.valueobjects.LivestockReportFilter;
import com.vantara.platform.reports.domain.model.valueobjects.ReportPeriod;

import java.time.LocalDate;

/**
 * Translates the query parameters of the report endpoints into domain filters.
 */
public final class ReportFilterAssembler {

    private ReportFilterAssembler() {
    }

    public static LivestockReportFilter toLivestockFilter(LocalDate startsOn, LocalDate endsOn, String lotId,
                                                          String breed, String sex, String status) {
        return new LivestockReportFilter(new ReportPeriod(startsOn, endsOn), lotId, breed, sex, status);
    }

    public static FeedingReportFilter toFeedingFilter(LocalDate startsOn, LocalDate endsOn, String lotId) {
        return new FeedingReportFilter(new ReportPeriod(startsOn, endsOn), lotId);
    }
}

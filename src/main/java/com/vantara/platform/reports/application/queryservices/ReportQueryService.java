package com.vantara.platform.reports.application.queryservices;

import com.vantara.platform.reports.domain.model.queries.GetFeedingReportQuery;
import com.vantara.platform.reports.domain.model.queries.GetLivestockReportQuery;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingReport;
import com.vantara.platform.reports.domain.model.valueobjects.LivestockReport;

/**
 * Read-side contract of the reports.
 */
public interface ReportQueryService {

    LivestockReport handle(GetLivestockReportQuery query);

    FeedingReport handle(GetFeedingReportQuery query);
}

package com.vantara.platform.reports.interfaces.rest.transform;

import com.vantara.platform.reports.domain.model.valueobjects.FeedingReport;
import com.vantara.platform.reports.interfaces.rest.resources.FeedingReportResource;

/**
 * Translates the feeding report into its HTTP resource.
 */
public final class FeedingReportResourceFromEntityAssembler {

    private FeedingReportResourceFromEntityAssembler() {
    }

    public static FeedingReportResource toResourceFromEntity(FeedingReport report) {
        return new FeedingReportResource(
                report.totalPlans(),
                report.animalsWithPlan(),
                report.totalRegisteredConsumption(),
                report.totalFeedingRecords(),
                report.consumptionUnits(),
                report.consumptionByFeed().stream()
                        .map(feed -> new FeedingReportResource.FeedConsumptionResource(
                                feed.feedName(), feed.quantity(), feed.unit()))
                        .toList(),
                report.consumptionByLot().stream()
                        .map(lot -> new FeedingReportResource.LotConsumptionResource(
                                lot.lotId(), lot.lotName(), lot.planName(), lot.quantity(), lot.unit()))
                        .toList());
    }
}

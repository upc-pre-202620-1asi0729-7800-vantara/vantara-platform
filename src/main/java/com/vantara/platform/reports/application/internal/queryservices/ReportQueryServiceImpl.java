package com.vantara.platform.reports.application.internal.queryservices;

import com.vantara.platform.reports.application.internal.outboundservices.livestock.LivestockDataSource;
import com.vantara.platform.reports.application.queryservices.ReportQueryService;
import com.vantara.platform.reports.domain.exceptions.ReportSourceUnavailableException;
import com.vantara.platform.reports.domain.model.queries.GetFeedingReportQuery;
import com.vantara.platform.reports.domain.model.queries.GetLivestockReportQuery;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingReport;
import com.vantara.platform.reports.domain.model.valueobjects.LivestockReport;
import com.vantara.platform.reports.domain.services.FeedingReportCalculator;
import com.vantara.platform.reports.domain.services.LivestockReportCalculator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class ReportQueryServiceImpl implements ReportQueryService {

    private final ObjectProvider<LivestockDataSource> livestockDataSource;

    public ReportQueryServiceImpl(ObjectProvider<LivestockDataSource> livestockDataSource) {
        this.livestockDataSource = livestockDataSource;
    }

    @Override
    public LivestockReport handle(GetLivestockReportQuery query) {
        var source = source();
        var rancherId = query.rancherId();
        return LivestockReportCalculator.calculate(
                rancherId,
                query.filter(),
                source.animalsOf(rancherId),
                source.lotsOf(rancherId));
    }

    @Override
    public FeedingReport handle(GetFeedingReportQuery query) {
        var source = source();
        var rancherId = query.rancherId();
        return FeedingReportCalculator.calculate(
                rancherId,
                query.filter(),
                source.lotsOf(rancherId),
                source.animalsOf(rancherId),
                source.feedingPlansOf(rancherId),
                source.feedingPlanItemsOf(rancherId),
                source.feedingLogsOf(rancherId));
    }

    private LivestockDataSource source() {
        var source = livestockDataSource.getIfAvailable();
        if (source == null) {
            throw new ReportSourceUnavailableException();
        }
        return source;
    }
}

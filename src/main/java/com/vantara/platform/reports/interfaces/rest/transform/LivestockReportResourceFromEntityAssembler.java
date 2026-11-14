package com.vantara.platform.reports.interfaces.rest.transform;

import com.vantara.platform.reports.domain.model.valueobjects.LivestockReport;
import com.vantara.platform.reports.interfaces.rest.resources.LivestockReportResource;

/**
 * Translates the livestock report into its HTTP resource.
 */
public final class LivestockReportResourceFromEntityAssembler {

    private LivestockReportResourceFromEntityAssembler() {
    }

    public static LivestockReportResource toResourceFromEntity(LivestockReport report) {
        return new LivestockReportResource(
                report.totalAnimals(),
                report.averageWeight(),
                report.totalLots(),
                report.males(),
                report.females(),
                report.animalsByLot().stream()
                        .map(LivestockReportResourceFromEntityAssembler::toLotSummary)
                        .toList(),
                report.animalsByBreed().stream()
                        .map(breed -> new LivestockReportResource.BreedCountResource(breed.breed(), breed.count()))
                        .toList());
    }

    private static LivestockReportResource.LotSummaryResource toLotSummary(LivestockReport.LotSummary lot) {
        return new LivestockReportResource.LotSummaryResource(
                lot.lotId(),
                lot.lotName(),
                lot.animalCount(),
                lot.averageWeight(),
                lot.males(),
                lot.females());
    }
}

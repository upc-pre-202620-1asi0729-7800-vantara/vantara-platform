package com.vantara.platform.reports.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/**
 * Feeding report returned by the API.
 */
public record FeedingReportResource(
        int totalPlans,
        int animalsWithPlan,
        BigDecimal totalRegisteredConsumption,
        int totalFeedingRecords,
        List<String> consumptionUnits,
        List<UnitConsumptionResource> totalConsumptionByUnit,
        List<FeedConsumptionResource> consumptionByFeed,
        List<LotConsumptionResource> consumptionByLot) {

    public record UnitConsumptionResource(String unit, BigDecimal quantity) {
    }

    public record FeedConsumptionResource(String feedName, BigDecimal quantity, String unit) {
    }

    public record LotConsumptionResource(
            String lotId,
            String lotName,
            String planName,
            BigDecimal quantity,
            String unit) {
    }
}

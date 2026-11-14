package com.vantara.platform.reports.domain.model.valueobjects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result of the feeding report of one rancher.
 *
 * @param totalPlans               feeding plans that match the filters
 * @param animalsWithPlan          distinct animals covered by those plans
 * @param totalRegisteredConsumption sum of the quantities of the matching records; mixes units when
 *                                 {@code consumptionUnits} has more than one entry
 * @param totalFeedingRecords      matching feeding records
 * @param consumptionUnits         distinct units of the matching records, sorted
 * @param consumptionByFeed        consumption per feed and unit
 * @param consumptionByLot         consumption per lot and unit
 */
public record FeedingReport(
        int totalPlans,
        int animalsWithPlan,
        BigDecimal totalRegisteredConsumption,
        int totalFeedingRecords,
        List<String> consumptionUnits,
        List<FeedConsumption> consumptionByFeed,
        List<LotConsumption> consumptionByLot) {

    /**
     * Quantity of one feed in one unit.
     *
     * @param feedName name of the feed
     * @param quantity total delivered
     * @param unit     unit of the quantity
     */
    public record FeedConsumption(String feedName, BigDecimal quantity, String unit) {
    }

    /**
     * Quantity delivered to one lot in one unit.
     *
     * @param lotId    identifier of the lot
     * @param lotName  name of the lot, or null when it does not belong to the rancher
     * @param planName plan of the lot with the earliest start date
     * @param quantity total delivered, zero when there are no records
     * @param unit     unit of the quantity, or null when there are no records
     */
    public record LotConsumption(String lotId, String lotName, String planName, BigDecimal quantity, String unit) {
    }
}

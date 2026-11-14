package com.vantara.platform.reports.domain.model.valueobjects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result of the feeding report of one rancher.
 *
 * @param totalPlans               feeding plans that match the filters
 * @param animalsWithPlan          distinct animals covered by those plans
 * @param totalRegisteredConsumption total quantity when there is zero or one unit; {@code null} when
 *                                   matching records use different units
 * @param totalFeedingRecords      matching feeding records
 * @param consumptionUnits         distinct units of the matching records, sorted
 * @param totalConsumptionByUnit   total quantity per unit
 * @param consumptionByFeed        consumption per feed and unit
 * @param consumptionByLot         consumption per lot and unit
 */
public record FeedingReport(
        int totalPlans,
        int animalsWithPlan,
        BigDecimal totalRegisteredConsumption,
        int totalFeedingRecords,
        List<String> consumptionUnits,
        List<UnitConsumption> totalConsumptionByUnit,
        List<FeedConsumption> consumptionByFeed,
        List<LotConsumption> consumptionByLot) {

    /**
     * Total quantity for one unit.
     *
     * @param unit unit of the quantity
     * @param quantity total delivered in that unit
     */
    public record UnitConsumption(String unit, BigDecimal quantity) {
    }

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
     * Quantity delivered for one plan in one lot and unit. Plans with no matching records have zero quantity
     * and a null unit.
     *
     * @param lotId    identifier of the lot
     * @param lotName  name of the lot, or null when it does not belong to the rancher
     * @param planName feeding plan associated with these records
     * @param quantity total delivered, zero when there are no records
     * @param unit     unit of the quantity, or null when there are no records
     */
    public record LotConsumption(String lotId, String lotName, String planName, BigDecimal quantity, String unit) {
    }
}

package com.vantara.platform.reports.domain.services;

import com.vantara.platform.reports.domain.model.valueobjects.AnimalSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingLogSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanItemSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingReport;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingReportFilter;
import com.vantara.platform.reports.domain.model.valueobjects.LotSnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Computes the feeding report of one rancher from owner-scoped snapshots.
 *
 * <p>Only plans and records whose lot belongs to the rancher are considered, so a foreign lot never leaks into
 * the totals. A feeding record counts when its plan is selected and its date falls in the period.</p>
 */
public final class FeedingReportCalculator {

    private static final Comparator<FeedingPlanSnapshot> PLAN_ORDER =
            Comparator.comparing(FeedingPlanSnapshot::startsOn).thenComparing(FeedingPlanSnapshot::id);

    private static final Comparator<FeedUnit> FEED_ORDER =
            Comparator.comparing(FeedUnit::feedName).thenComparing(FeedUnit::unit);

    private FeedingReportCalculator() {
    }

    public static FeedingReport calculate(String rancherId, FeedingReportFilter filter,
                                          List<LotSnapshot> lots, List<AnimalSnapshot> animals,
                                          List<FeedingPlanSnapshot> plans, List<FeedingPlanItemSnapshot> items,
                                          List<FeedingLogSnapshot> logs) {
        var lotNames = lots.stream()
                .filter(lot -> rancherId.equals(lot.rancherId()))
                .collect(Collectors.toMap(LotSnapshot::id, LotSnapshot::name, (first, second) -> first));

        var ownedAnimals = animals.stream()
                .filter(animal -> rancherId.equals(animal.rancherId()))
                .toList();

        var selectedPlans = plans.stream()
                .filter(plan -> lotNames.containsKey(plan.lotId()))
                .filter(plan -> filter.period().contains(plan.startsOn()))
                .filter(plan -> filter.lotId() == null || filter.lotId().equals(plan.lotId()))
                .toList();

        var planIds = selectedPlans.stream().map(FeedingPlanSnapshot::id).collect(Collectors.toSet());
        Map<String, String> planIdByItemId = items.stream()
                .filter(item -> planIds.contains(item.feedingPlanId()))
                .collect(Collectors.toMap(FeedingPlanItemSnapshot::id, FeedingPlanItemSnapshot::feedingPlanId,
                        (first, second) -> first));

        var selectedLogs = logs.stream()
                .filter(log -> lotNames.containsKey(log.lotId()))
                .filter(log -> planIdByItemId.containsKey(log.planItemId()))
                .filter(log -> filter.period().contains(log.feedAt().toLocalDate()))
                .filter(log -> filter.lotId() == null || filter.lotId().equals(log.lotId()))
                .toList();

        var animalsWithPlan = countAnimalsWithPlan(selectedPlans, ownedAnimals);

        var totalConsumption = selectedLogs.stream()
                .map(FeedingLogSnapshot::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var units = selectedLogs.stream()
                .map(FeedingLogSnapshot::unit)
                .distinct()
                .sorted()
                .toList();

        var consumptionByFeed = selectedLogs.stream()
                .collect(Collectors.groupingBy(log -> new FeedUnit(log.feedName(), log.unit()),
                        Collectors.reducing(BigDecimal.ZERO, FeedingLogSnapshot::quantity, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(FEED_ORDER))
                .map(entry -> new FeedingReport.FeedConsumption(
                        entry.getKey().feedName(), entry.getValue(), entry.getKey().unit()))
                .toList();

        var consumptionByLot = buildConsumptionByLot(selectedPlans, selectedLogs, lotNames);

        return new FeedingReport(
                selectedPlans.size(),
                animalsWithPlan,
                totalConsumption,
                selectedLogs.size(),
                units,
                consumptionByFeed,
                consumptionByLot);
    }

    private static int countAnimalsWithPlan(List<FeedingPlanSnapshot> plans, List<AnimalSnapshot> ownedAnimals) {
        Set<String> ownedAnimalIds = ownedAnimals.stream().map(AnimalSnapshot::id).collect(Collectors.toSet());
        Set<String> covered = new HashSet<>();
        for (var plan : plans) {
            if (plan.animalId() != null) {
                if (ownedAnimalIds.contains(plan.animalId())) {
                    covered.add(plan.animalId());
                }
            } else {
                ownedAnimals.stream()
                        .filter(animal -> plan.lotId().equals(animal.lotId()))
                        .forEach(animal -> covered.add(animal.id()));
            }
        }
        return covered.size();
    }

    private static List<FeedingReport.LotConsumption> buildConsumptionByLot(
            List<FeedingPlanSnapshot> plans, List<FeedingLogSnapshot> logs, Map<String, String> lotNames) {
        var lotIds = plans.stream()
                .map(FeedingPlanSnapshot::lotId)
                .collect(Collectors.toCollection(TreeSet::new));

        var rows = new ArrayList<FeedingReport.LotConsumption>();
        for (var lotId : lotIds) {
            var planName = plans.stream()
                    .filter(plan -> lotId.equals(plan.lotId()))
                    .min(PLAN_ORDER)
                    .map(FeedingPlanSnapshot::name)
                    .orElse(null);
            var lotName = lotNames.get(lotId);
            var lotLogs = logs.stream().filter(log -> lotId.equals(log.lotId())).toList();

            if (lotLogs.isEmpty()) {
                rows.add(new FeedingReport.LotConsumption(lotId, lotName, planName, BigDecimal.ZERO, null));
                continue;
            }
            lotLogs.stream()
                    .collect(Collectors.groupingBy(FeedingLogSnapshot::unit, TreeMap::new,
                            Collectors.reducing(BigDecimal.ZERO, FeedingLogSnapshot::quantity, BigDecimal::add)))
                    .forEach((unit, quantity) -> rows.add(
                            new FeedingReport.LotConsumption(lotId, lotName, planName, quantity, unit)));
        }
        return rows;
    }

    private record FeedUnit(String feedName, String unit) {
    }
}

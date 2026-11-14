package com.vantara.platform.reports.domain.services;

import com.vantara.platform.reports.domain.model.valueobjects.AnimalSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.LivestockReport;
import com.vantara.platform.reports.domain.model.valueobjects.LivestockReportFilter;
import com.vantara.platform.reports.domain.model.valueobjects.LotSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Computes the livestock report of one rancher from owner-scoped snapshots.
 *
 * <p>Sex is compared case-insensitively. Only {@code Macho} and {@code Hembra} count as males and females;
 * other values still count in the totals.</p>
 */
public final class LivestockReportCalculator {

    private static final String MALE = "Macho";
    private static final String FEMALE = "Hembra";
    private static final int SCALE = 2;

    private LivestockReportCalculator() {
    }

    public static LivestockReport calculate(String rancherId, LivestockReportFilter filter,
                                            List<AnimalSnapshot> animals, List<LotSnapshot> lots) {
        var lotNames = lots.stream()
                .filter(lot -> rancherId.equals(lot.rancherId()))
                .collect(Collectors.toMap(LotSnapshot::id, LotSnapshot::name, (first, second) -> first));

        var selected = animals.stream()
                .filter(animal -> rancherId.equals(animal.rancherId()))
                .filter(animal -> filter.period().contains(animal.registeredAt()))
                .filter(animal -> filter.lotId() == null || filter.lotId().equals(animal.lotId()))
                .filter(animal -> filter.breed() == null || filter.breed().equals(animal.breed()))
                .filter(animal -> filter.sex() == null || filter.sex().equalsIgnoreCase(animal.sex()))
                .filter(animal -> filter.status() == null || filter.status().equals(animal.status()))
                .toList();

        var lotSummaries = selected.stream()
                .collect(Collectors.groupingBy(AnimalSnapshot::lotId))
                .entrySet().stream()
                .map(entry -> new LivestockReport.LotSummary(
                        entry.getKey(),
                        lotNames.get(entry.getKey()),
                        entry.getValue().size(),
                        average(entry.getValue()),
                        countSex(entry.getValue(), MALE),
                        countSex(entry.getValue(), FEMALE)))
                .sorted(Comparator.comparing(LivestockReport.LotSummary::lotName,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(LivestockReport.LotSummary::lotId))
                .toList();

        Map<String, Long> countByBreed = selected.stream()
                .collect(Collectors.groupingBy(AnimalSnapshot::breed, TreeMap::new, Collectors.counting()));
        var breedCounts = countByBreed.entrySet().stream()
                .map(entry -> new LivestockReport.BreedCount(entry.getKey(), entry.getValue().intValue()))
                .toList();

        var totalLots = (int) selected.stream().map(AnimalSnapshot::lotId).distinct().count();

        return new LivestockReport(
                selected.size(),
                average(selected),
                totalLots,
                countSex(selected, MALE),
                countSex(selected, FEMALE),
                lotSummaries,
                breedCounts);
    }

    private static BigDecimal average(List<AnimalSnapshot> animals) {
        if (animals.isEmpty()) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        var total = animals.stream()
                .map(AnimalSnapshot::weight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(animals.size()), SCALE, RoundingMode.HALF_UP);
    }

    private static int countSex(List<AnimalSnapshot> animals, String sex) {
        return (int) animals.stream()
                .filter(animal -> sex.equalsIgnoreCase(animal.sex()))
                .count();
    }
}

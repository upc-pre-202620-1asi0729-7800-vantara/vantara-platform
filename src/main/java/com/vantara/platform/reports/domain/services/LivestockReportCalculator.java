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
 * <p>Sex is compared case-insensitively. {@code Male}/{@code Macho} and {@code Female}/{@code Hembra}
 * are treated as equivalent; other values still count in the totals.</p>
 */
public final class LivestockReportCalculator {

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
                .filter(animal -> filter.sex() == null
                        || normalizeSex(filter.sex()).equals(normalizeSex(animal.sex())))
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
                        countSex(entry.getValue(), true),
                        countSex(entry.getValue(), false)))
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
                countSex(selected, true),
                countSex(selected, false),
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

    private static int countSex(List<AnimalSnapshot> animals, boolean male) {
        return (int) animals.stream()
                .filter(animal -> male ? isMale(animal.sex()) : isFemale(animal.sex()))
                .count();
    }

    private static boolean isMale(String sex) {
        return "male".equals(normalizeSex(sex));
    }

    private static boolean isFemale(String sex) {
        return "female".equals(normalizeSex(sex));
    }

    private static String normalizeSex(String sex) {
        if (sex == null) {
            return "";
        }
        return switch (sex.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "male", "macho" -> "male";
            case "female", "hembra" -> "female";
            default -> sex.trim().toLowerCase(java.util.Locale.ROOT);
        };
    }
}

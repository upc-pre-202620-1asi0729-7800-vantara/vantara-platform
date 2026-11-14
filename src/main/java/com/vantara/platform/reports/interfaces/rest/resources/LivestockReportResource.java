package com.vantara.platform.reports.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/**
 * Livestock report returned by the API.
 */
public record LivestockReportResource(
        int totalAnimals,
        BigDecimal averageWeight,
        int totalLots,
        int males,
        int females,
        List<LotSummaryResource> animalsByLot,
        List<BreedCountResource> animalsByBreed) {

    public record LotSummaryResource(
            String lotId,
            String lotName,
            int animalCount,
            BigDecimal averageWeight,
            int males,
            int females) {
    }

    public record BreedCountResource(String breed, int count) {
    }
}

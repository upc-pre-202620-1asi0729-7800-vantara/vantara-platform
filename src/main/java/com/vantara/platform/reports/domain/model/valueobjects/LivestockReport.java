package com.vantara.platform.reports.domain.model.valueobjects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result of the livestock report of one rancher.
 *
 * @param totalAnimals   animals that match the filters
 * @param averageWeight  average weight in kilograms, two decimals
 * @param totalLots      distinct lots of the matching animals
 * @param males          matching animals whose sex is {@code Macho}
 * @param females        matching animals whose sex is {@code Hembra}
 * @param animalsByLot   breakdown by lot
 * @param animalsByBreed breakdown by breed
 */
public record LivestockReport(
        int totalAnimals,
        BigDecimal averageWeight,
        int totalLots,
        int males,
        int females,
        List<LotSummary> animalsByLot,
        List<BreedCount> animalsByBreed) {

    /**
     * Summary of the animals kept in one lot.
     *
     * @param lotId         identifier of the lot
     * @param lotName       name of the lot, or null when it does not belong to the rancher
     * @param animalCount   matching animals in the lot
     * @param averageWeight average weight in kilograms, two decimals
     * @param males         matching males in the lot
     * @param females       matching females in the lot
     */
    public record LotSummary(
            String lotId,
            String lotName,
            int animalCount,
            BigDecimal averageWeight,
            int males,
            int females) {
    }

    /**
     * Number of matching animals of one breed.
     *
     * @param breed breed
     * @param count animals of that breed
     */
    public record BreedCount(String breed, int count) {
    }
}

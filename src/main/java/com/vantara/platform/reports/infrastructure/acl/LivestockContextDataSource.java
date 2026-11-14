package com.vantara.platform.reports.infrastructure.acl;

import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.reports.application.internal.outboundservices.livestock.LivestockDataSource;
import com.vantara.platform.reports.domain.model.valueobjects.AnimalSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingLogSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanItemSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.LotSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Anti-corruption adapter that feeds the reports with livestock data. It reads only through the public
 * {@link LivestockContextFacade} and never touches livestock repositories or entities. Each operation keeps
 * the records of the requested rancher, so the reports never see another rancher's data.
 */
@Component
public class LivestockContextDataSource implements LivestockDataSource {

    private final LivestockContextFacade livestock;

    public LivestockContextDataSource(LivestockContextFacade livestock) {
        this.livestock = livestock;
    }

    @Override
    public List<AnimalSnapshot> animalsOf(String rancherId) {
        return livestock.listAnimalsOf(rancherId).stream()
                .filter(animal -> rancherId.equals(animal.rancherId()))
                .map(animal -> new AnimalSnapshot(
                        animal.id(),
                        animal.rancherId(),
                        animal.lotId(),
                        animal.breed(),
                        animal.sex(),
                        animal.status(),
                        animal.weight(),
                        animal.registeredAt()))
                .toList();
    }

    @Override
    public List<LotSnapshot> lotsOf(String rancherId) {
        return livestock.listLotsOf(rancherId).stream()
                .filter(lot -> rancherId.equals(lot.rancherId()))
                .map(lot -> new LotSnapshot(lot.id(), lot.rancherId(), lot.name()))
                .toList();
    }

    @Override
    public List<FeedingPlanSnapshot> feedingPlansOf(String rancherId) {
        return livestock.listFeedingPlansOf(rancherId).stream()
                .map(plan -> new FeedingPlanSnapshot(
                        plan.id(), plan.lotId(), plan.animalId(), plan.name(), plan.startsOn()))
                .toList();
    }

    @Override
    public List<FeedingPlanItemSnapshot> feedingPlanItemsOf(String rancherId) {
        return livestock.listFeedingPlanItemsOf(rancherId).stream()
                .map(item -> new FeedingPlanItemSnapshot(item.id(), item.feedingPlanId()))
                .toList();
    }

    @Override
    public List<FeedingLogSnapshot> feedingLogsOf(String rancherId) {
        return livestock.listFeedingLogsOf(rancherId).stream()
                .map(log -> new FeedingLogSnapshot(
                        log.planItemId(), log.lotId(), log.feedName(), log.feedAt(), log.quantity(), log.unit()))
                .toList();
    }
}

package com.vantara.platform.reports.application.internal.outboundservices.livestock;

import com.vantara.platform.reports.domain.model.valueobjects.AnimalSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingLogSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanItemSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.FeedingPlanSnapshot;
import com.vantara.platform.reports.domain.model.valueobjects.LotSnapshot;

import java.util.List;

/**
 * Outbound port that reads the livestock data of one rancher.
 *
 * <p>Every operation receives the rancher resolved from the authenticated account and must return only the
 * records that rancher owns. Reports never query another context's repositories or entities. The implementation
 * belongs to the infrastructure layer and is provided by the livestock integration; until then, reports answer
 * {@code 503}.</p>
 */
public interface LivestockDataSource {

    List<AnimalSnapshot> animalsOf(String rancherId);

    List<LotSnapshot> lotsOf(String rancherId);

    List<FeedingPlanSnapshot> feedingPlansOf(String rancherId);

    List<FeedingPlanItemSnapshot> feedingPlanItemsOf(String rancherId);

    List<FeedingLogSnapshot> feedingLogsOf(String rancherId);
}

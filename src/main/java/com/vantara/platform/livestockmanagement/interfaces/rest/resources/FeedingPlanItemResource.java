package com.vantara.platform.livestockmanagement.interfaces.rest.resources;

import com.vantara.platform.livestockmanagement.domain.model.entities.FeedingPlanItem;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record FeedingPlanItemResource(String id, @NotBlank String feedingPlanId, @NotBlank String feedName,
                                      @NotNull @DecimalMin("0") BigDecimal dailyQuantity, @NotBlank String unit) {
    public FeedingPlanItem toDomain(String generatedId) { return FeedingPlanItem.create(id == null || id.isBlank() ? generatedId : id, feedingPlanId, feedName, dailyQuantity, unit); }
    public static FeedingPlanItemResource from(FeedingPlanItem item) { return new FeedingPlanItemResource(item.getId(), item.getFeedingPlanId(), item.getFeedName(), item.getDailyQuantity(), item.getUnit()); }
}

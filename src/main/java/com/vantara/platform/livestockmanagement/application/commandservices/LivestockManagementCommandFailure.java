package com.vantara.platform.livestockmanagement.application.commandservices;

/** Expected business failures returned by livestock command operations. */
public sealed interface LivestockManagementCommandFailure permits LivestockManagementCommandFailure.DuplicateEarTag,
        LivestockManagementCommandFailure.AnimalNotFound, LivestockManagementCommandFailure.LotNotFound,
        LivestockManagementCommandFailure.FeedingPlanNotFound, LivestockManagementCommandFailure.InvalidRelationship {
    String messageKey();
    record DuplicateEarTag() implements LivestockManagementCommandFailure { public String messageKey() { return "livestock.animal.error.duplicate-ear-tag"; } }
    record AnimalNotFound(String animalId) implements LivestockManagementCommandFailure { public String messageKey() { return "livestock.animal.error.not-found"; } }
    record LotNotFound(String lotId) implements LivestockManagementCommandFailure { public String messageKey() { return "livestock.lot.error.not-found"; } }
    record FeedingPlanNotFound(String feedingPlanId) implements LivestockManagementCommandFailure { public String messageKey() { return "livestock.feeding-plan.error.not-found"; } }
    record InvalidRelationship(String detail) implements LivestockManagementCommandFailure { public String messageKey() { return "livestock.error.invalid-relationship"; } }
}

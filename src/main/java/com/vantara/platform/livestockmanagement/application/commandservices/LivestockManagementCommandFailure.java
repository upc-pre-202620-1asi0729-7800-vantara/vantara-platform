
package com.vantara.platform.livestockmanagement.application.commandservices;

/**
 * Expected business failures returned by livestock command operations.
 */
public sealed interface LivestockManagementCommandFailure
        permits LivestockManagementCommandFailure.DuplicateEarTag,
        LivestockManagementCommandFailure.AnimalNotFound,
        LivestockManagementCommandFailure.LotNotFound,
        LivestockManagementCommandFailure.FeedingPlanNotFound,
        LivestockManagementCommandFailure.InvalidRelationship {

    String messageKey();

    record DuplicateEarTag()
            implements LivestockManagementCommandFailure {

        @Override
        public String messageKey() {
            return "livestock.animal.error.duplicate-ear-tag";
        }
    }

    record AnimalNotFound(String animalId)
            implements LivestockManagementCommandFailure {

        public AnimalNotFound {
            require(animalId, "animalId");
            animalId = animalId.trim();
        }

        @Override
        public String messageKey() {
            return "livestock.animal.error.not-found";
        }
    }

    record LotNotFound(String lotId)
            implements LivestockManagementCommandFailure {

        public LotNotFound {
            require(lotId, "lotId");
            lotId = lotId.trim();
        }

        @Override
        public String messageKey() {
            return "livestock.lot.error.not-found";
        }
    }

    record FeedingPlanNotFound(String feedingPlanId)
            implements LivestockManagementCommandFailure {

        public FeedingPlanNotFound {
            require(feedingPlanId, "feedingPlanId");
            feedingPlanId = feedingPlanId.trim();
        }

        @Override
        public String messageKey() {
            return "livestock.feeding-plan.error.not-found";
        }
    }

    record InvalidRelationship(String detail)
            implements LivestockManagementCommandFailure {

        public InvalidRelationship {
            require(detail, "detail");
            detail = detail.trim();
        }

        @Override
        public String messageKey() {
            return "livestock.error.invalid-relationship";
        }
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }
}

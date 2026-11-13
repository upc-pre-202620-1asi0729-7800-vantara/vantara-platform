package com.vantara.platform.livestockmanagement.interfaces.rest.transform;

import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandFailure;
import com.vantara.platform.shared.application.result.ApplicationError;

/**
 * Translates the expected failures of livestock commands into the shared {@link ApplicationError},
 * so that they leave the API as an {@code ErrorResource} with the right HTTP status.
 */
public final class LivestockFailureAssembler {

    private LivestockFailureAssembler() {
    }

    /**
     * @param failure expected failure returned by a livestock command
     * @return the matching application error (404 for missing resources, 409 for a duplicate ear
     *         tag, 422 for an inconsistent relationship)
     */
    public static ApplicationError toApplicationError(LivestockManagementCommandFailure failure) {
        return switch (failure) {
            case LivestockManagementCommandFailure.DuplicateEarTag ignored ->
                    ApplicationError.conflict("animal", "The ear tag is already registered for this rancher");
            case LivestockManagementCommandFailure.AnimalNotFound(var animalId) ->
                    ApplicationError.notFound("animal", animalId);
            case LivestockManagementCommandFailure.LotNotFound(var lotId) ->
                    ApplicationError.notFound("lot", lotId);
            case LivestockManagementCommandFailure.FeedingPlanNotFound(var planId) ->
                    ApplicationError.notFound("feedingPlan", planId);
            case LivestockManagementCommandFailure.InvalidRelationship(var detail) ->
                    ApplicationError.businessRuleViolation("relationship", detail);
        };
    }
}

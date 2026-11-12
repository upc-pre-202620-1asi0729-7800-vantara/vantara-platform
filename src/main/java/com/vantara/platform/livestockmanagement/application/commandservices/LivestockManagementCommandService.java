package com.vantara.platform.livestockmanagement.application.commandservices;

import com.vantara.platform.livestockmanagement.application.commands.*;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.*;
import com.vantara.platform.livestockmanagement.domain.model.entities.*;
import com.vantara.platform.shared.application.result.Result;

/** Command contract for livestock registration and lifecycle operations. */
public interface LivestockManagementCommandService {
    Result<Animal, LivestockManagementCommandFailure> handle(CreateAnimalCommand command);
    Result<Animal, LivestockManagementCommandFailure> handle(UpdateAnimalCommand command);
    Result<Boolean, LivestockManagementCommandFailure> handle(DeleteAnimalCommand command);
    Result<Lot, LivestockManagementCommandFailure> handle(CreateLotCommand command);
    Result<Movement, LivestockManagementCommandFailure> handle(RecordMovementCommand command);
    Result<FeedingPlan, LivestockManagementCommandFailure> handle(CreateFeedingPlanCommand command);
    Result<FeedingPlanItem, LivestockManagementCommandFailure> handle(CreateFeedingPlanItemCommand command);
    Result<FeedingLog, LivestockManagementCommandFailure> handle(RecordFeedingCommand command);
}


package com.vantara.platform.livestockmanagement.application.commandservices;

import com.vantara.platform.livestockmanagement.application.commands.CreateAnimalCommand;
import com.vantara.platform.livestockmanagement.application.commands.UpdateAnimalCommand;
import com.vantara.platform.livestockmanagement.application.commands.DeleteAnimalCommand;
import com.vantara.platform.livestockmanagement.application.commands.CreateLotCommand;
import com.vantara.platform.livestockmanagement.application.commands.RecordMovementCommand;
import com.vantara.platform.livestockmanagement.application.commands.CreateFeedingPlanCommand;
import com.vantara.platform.livestockmanagement.application.commands.RecordFeedingCommand;

import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Movement;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingLog;

import com.vantara.platform.shared.application.result.Result;

/**
 * Command contract for livestock registration and lifecycle operations.
 */
public interface LivestockManagementCommandService {

    Result<Animal, LivestockManagementCommandFailure> handle(
            CreateAnimalCommand command
    );

    Result<Animal, LivestockManagementCommandFailure> handle(
            UpdateAnimalCommand command
    );

    Result<Boolean, LivestockManagementCommandFailure> handle(
            DeleteAnimalCommand command
    );

    Result<Lot, LivestockManagementCommandFailure> handle(
            CreateLotCommand command
    );

    Result<Movement, LivestockManagementCommandFailure> handle(
            RecordMovementCommand command
    );

    Result<FeedingPlan, LivestockManagementCommandFailure> handle(
            CreateFeedingPlanCommand command
    );

    Result<FeedingLog, LivestockManagementCommandFailure> handle(
            RecordFeedingCommand command
    );
}

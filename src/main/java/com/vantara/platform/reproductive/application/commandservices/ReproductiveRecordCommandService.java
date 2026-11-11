package com.vantara.platform.reproductive.application.commandservices;

import com.vantara.platform.reproductive.domain.model.commands.RegisterCalvingCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterDryOffCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterPregnancyCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterWeaningCommand;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;

/**
 * Application service contract for reproductive record commands.
 */
public interface ReproductiveRecordCommandService {

    /**
     * Handles the registration of a pregnancy.
     *
     * @param command pregnancy registration command
     * @return identifier of the new reproductive record, or an application error
     * @see RegisterPregnancyCommand
     */
    Result<Long, ApplicationError> handle(RegisterPregnancyCommand command);

    /**
     * Handles the registration of the calving that ends a pregnancy, optionally registering the calf in livestock.
     *
     * @param command calving registration command
     * @return identifier of the updated reproductive record, or an application error
     * @see RegisterCalvingCommand
     */
    Result<Long, ApplicationError> handle(RegisterCalvingCommand command);

    /**
     * Handles the registration of the dry-off of a pregnant animal.
     *
     * @param command dry-off registration command
     * @return identifier of the updated reproductive record, or an application error
     * @see RegisterDryOffCommand
     */
    Result<Long, ApplicationError> handle(RegisterDryOffCommand command);

    /**
     * Handles the registration of the weaning of the calf, updating the calf in livestock.
     *
     * @param command weaning registration command
     * @return identifier of the updated reproductive record, or an application error
     * @see RegisterWeaningCommand
     */
    Result<Long, ApplicationError> handle(RegisterWeaningCommand command);
}

package com.vantara.platform.reproductive.application.commandservices;

import com.vantara.platform.reproductive.domain.model.commands.RegisterPregnancyCommand;
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
}

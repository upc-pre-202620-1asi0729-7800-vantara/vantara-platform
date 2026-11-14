package com.vantara.platform.profilemanagement.application.commandservices;

import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.domain.model.commands.CreateOrUpdateProfileCommand;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;

/**
 * Write-side contract of the profile use cases.
 */
public interface ProfileCommandService {

    /**
     * Creates the profile of an account, or updates the existing one.
     */
    Result<Profile, ApplicationError> handle(CreateOrUpdateProfileCommand command);
}

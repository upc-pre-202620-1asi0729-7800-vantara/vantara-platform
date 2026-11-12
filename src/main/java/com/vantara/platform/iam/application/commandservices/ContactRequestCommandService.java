package com.vantara.platform.iam.application.commandservices;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.model.commands.SendContactRequestCommand;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;

/**
 * Write-side contract of the contact form use case.
 */
public interface ContactRequestCommandService {

    Result<ContactRequest, ApplicationError> handle(SendContactRequestCommand command);
}

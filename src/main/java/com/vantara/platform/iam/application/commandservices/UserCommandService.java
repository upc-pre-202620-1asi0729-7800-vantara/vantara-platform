package com.vantara.platform.iam.application.commandservices;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.commands.RecoverPasswordCommand;
import com.vantara.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vantara.platform.iam.domain.model.commands.SendVerificationCodeCommand;
import com.vantara.platform.iam.domain.model.commands.SignInCommand;
import com.vantara.platform.iam.domain.model.commands.SignUpCommand;
import com.vantara.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;

/**
 * Write-side contract of the user use cases.
 */
public interface UserCommandService {

    Result<User, ApplicationError> handle(SignUpCommand command);

    Result<AuthenticatedUser, ApplicationError> handle(SignInCommand command);

    Result<User, ApplicationError> handle(VerifyEmailCommand command);

    Result<Acknowledgement, ApplicationError> handle(SendVerificationCodeCommand command);

    Result<Acknowledgement, ApplicationError> handle(RequestPasswordRecoveryCommand command);

    Result<User, ApplicationError> handle(RecoverPasswordCommand command);
}

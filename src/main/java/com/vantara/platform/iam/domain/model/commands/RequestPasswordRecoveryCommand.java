package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to request a new password ("request new password").
 *
 * @param email email address of the account
 */
public record RequestPasswordRecoveryCommand(String email) {
}

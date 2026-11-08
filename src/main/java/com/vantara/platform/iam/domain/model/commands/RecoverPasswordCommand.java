package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to set a new password using a recovery code ("recover password").
 *
 * @param email       email address of the account
 * @param code        six-digit recovery code received by email
 * @param newPassword new plain-text password
 */
public record RecoverPasswordCommand(String email, String code, String newPassword) {
}

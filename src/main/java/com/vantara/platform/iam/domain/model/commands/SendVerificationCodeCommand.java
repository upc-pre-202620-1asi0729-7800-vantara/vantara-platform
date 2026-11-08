package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to send a new email verification code to a pending account ("send email code").
 *
 * @param email email address of the account
 */
public record SendVerificationCodeCommand(String email) {
}

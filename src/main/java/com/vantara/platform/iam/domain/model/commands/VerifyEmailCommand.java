package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to verify the email address of an account ("verify email").
 *
 * @param email email address to verify
 * @param code  six-digit code received by email
 */
public record VerifyEmailCommand(String email, String code) {
}

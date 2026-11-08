package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to register a new user ("register new user").
 *
 * @param email    email address of the new user
 * @param password plain-text password
 * @param role     requested role name, or {@code null} to use the default role
 */
public record SignUpCommand(String email, String password, String role) {
}

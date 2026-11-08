package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to authenticate a user ("log in").
 *
 * @param email    email address of the user
 * @param password plain-text password
 */
public record SignInCommand(String email, String password) {
}

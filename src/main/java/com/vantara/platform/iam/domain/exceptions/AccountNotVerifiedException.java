package com.vantara.platform.iam.domain.exceptions;

/**
 * Thrown when an account whose email address is not verified tries to sign in.
 */
public class AccountNotVerifiedException extends RuntimeException {

    public AccountNotVerifiedException(String email) {
        super("Account %s has not verified its email address".formatted(email));
    }
}

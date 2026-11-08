package com.vantara.platform.iam.domain.exceptions;

/**
 * Thrown when the email address of an account is verified a second time.
 */
public class EmailAlreadyVerifiedException extends RuntimeException {

    public EmailAlreadyVerifiedException(String email) {
        super("Email address %s is already verified".formatted(email));
    }
}

package com.vantara.platform.iam.domain.model.valueobjects;

/**
 * Lifecycle status of a user account.
 */
public enum AccountStatus {
    /** The account was registered but the email address has not been verified yet. */
    PENDING_VERIFICATION,
    /** The email address was verified and the account can sign in. */
    ACTIVE
}

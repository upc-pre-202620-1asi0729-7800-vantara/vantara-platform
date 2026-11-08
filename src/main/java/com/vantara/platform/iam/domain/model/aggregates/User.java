package com.vantara.platform.iam.domain.model.aggregates;

import com.vantara.platform.iam.domain.exceptions.AccountNotVerifiedException;
import com.vantara.platform.iam.domain.exceptions.EmailAlreadyVerifiedException;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.events.EmailVerifiedEvent;
import com.vantara.platform.iam.domain.model.events.PasswordRecoveredEvent;
import com.vantara.platform.iam.domain.model.events.SignInSucceededEvent;
import com.vantara.platform.iam.domain.model.events.UserSignedUpEvent;
import com.vantara.platform.iam.domain.model.valueobjects.AccountStatus;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate root of an account: identity, credentials, roles and lifecycle status.
 */
public class User extends AbstractDomainAggregateRoot<User> {

    private Long id;
    private EmailAddress email;
    private PasswordHash passwordHash;
    private final Set<Role> roles = new LinkedHashSet<>();
    private AccountStatus status;
    private Instant lastSignInAt;

    private User() {
    }

    /**
     * Factory method that registers a new account pending email verification.
     *
     * @param email        email address of the new user
     * @param passwordHash already encrypted password
     * @param role         role requested at registration
     * @param now          registration instant
     * @return the new user with a {@link UserSignedUpEvent} recorded
     */
    public static User register(EmailAddress email, PasswordHash passwordHash, Role role, Instant now) {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        Objects.requireNonNull(role, "Role cannot be null");
        Objects.requireNonNull(now, "Registration instant cannot be null");

        var user = new User();
        user.email = email;
        user.passwordHash = passwordHash;
        user.status = AccountStatus.PENDING_VERIFICATION;
        user.roles.add(role);
        user.registerDomainEvent(new UserSignedUpEvent(email.address(), role.getStringName(), now));
        return user;
    }

    /**
     * Rebuilds a user from its persisted state. It does not record any domain event.
     */
    public static User reconstitute(
            Long id,
            EmailAddress email,
            PasswordHash passwordHash,
            Collection<Role> roles,
            AccountStatus status,
            Instant lastSignInAt
    ) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("Invalid user id");
        }
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one role");
        }

        var user = new User();
        user.id = id;
        user.email = email;
        user.passwordHash = passwordHash;
        user.roles.addAll(roles);
        user.status = status;
        user.lastSignInAt = lastSignInAt;
        return user;
    }

    /**
     * Marks the email address as verified and activates the account ("account approved").
     *
     * @param now verification instant
     * @throws EmailAlreadyVerifiedException when the account is already active
     */
    public void verifyEmail(Instant now) {
        if (status == AccountStatus.ACTIVE) {
            throw new EmailAlreadyVerifiedException(email.address());
        }
        this.status = AccountStatus.ACTIVE;
        registerDomainEvent(new EmailVerifiedEvent(email.address(), now));
    }

    /**
     * Records a successful sign-in ("log in successful").
     *
     * @param now sign-in instant
     * @throws AccountNotVerifiedException when the email address was not verified yet
     */
    public void recordSignIn(Instant now) {
        if (!canSignIn()) {
            throw new AccountNotVerifiedException(email.address());
        }
        this.lastSignInAt = now;
        registerDomainEvent(new SignInSucceededEvent(email.address(), now));
    }

    /**
     * Replaces the password after a successful recovery ("password recovered").
     *
     * @param newPasswordHash already encrypted new password
     * @param now             recovery instant
     */
    public void recoverPassword(PasswordHash newPasswordHash, Instant now) {
        Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
        this.passwordHash = newPasswordHash;
        registerDomainEvent(new PasswordRecoveredEvent(email.address(), now));
    }

    /**
     * Grants an additional role to the user.
     */
    public void addRole(Role role) {
        this.roles.add(Objects.requireNonNull(role, "Role cannot be null"));
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean canSignIn() {
        return status == AccountStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getLastSignInAt() {
        return lastSignInAt;
    }
}

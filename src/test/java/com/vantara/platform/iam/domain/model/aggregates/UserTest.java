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
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-11-07T10:00:00Z");

    private static User newUser() {
        return User.register(
                new EmailAddress("rancher@vantara.pe"),
                new PasswordHash("hash"),
                new Role(Roles.ROLE_RANCHER),
                NOW);
    }

    @Test
    void registerCreatesPendingAccountAndRecordsEvent() {
        var user = newUser();

        assertThat(user.getStatus()).isEqualTo(AccountStatus.PENDING_VERIFICATION);
        assertThat(user.canSignIn()).isFalse();
        assertThat(user.getRoles()).containsExactly(new Role(Roles.ROLE_RANCHER));
        assertThat(user.domainEvents())
                .singleElement()
                .isEqualTo(new UserSignedUpEvent("rancher@vantara.pe", "ROLE_RANCHER", NOW));
    }

    @Test
    void cannotSignInBeforeVerifyingEmail() {
        var user = newUser();

        assertThatThrownBy(() -> user.recordSignIn(NOW)).isInstanceOf(AccountNotVerifiedException.class);
    }

    @Test
    void verifyEmailActivatesAccountOnlyOnce() {
        var user = newUser();

        user.verifyEmail(NOW);

        assertThat(user.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(user.domainEvents()).contains(new EmailVerifiedEvent("rancher@vantara.pe", NOW));
        assertThatThrownBy(() -> user.verifyEmail(NOW)).isInstanceOf(EmailAlreadyVerifiedException.class);
    }

    @Test
    void activeAccountCanSignInAndRecordsLastSignIn() {
        var user = newUser();
        user.verifyEmail(NOW);

        user.recordSignIn(NOW.plusSeconds(60));

        assertThat(user.getLastSignInAt()).isEqualTo(NOW.plusSeconds(60));
        assertThat(user.domainEvents()).contains(new SignInSucceededEvent("rancher@vantara.pe", NOW.plusSeconds(60)));
    }

    @Test
    void recoverPasswordReplacesHashAndRecordsEvent() {
        var user = newUser();

        user.recoverPassword(new PasswordHash("new-hash"), NOW);

        assertThat(user.getPasswordHash()).isEqualTo(new PasswordHash("new-hash"));
        assertThat(user.domainEvents()).contains(new PasswordRecoveredEvent("rancher@vantara.pe", NOW));
    }

    @Test
    void rolesAreNotModifiableFromOutside() {
        var user = newUser();

        assertThatThrownBy(() -> user.getRoles().add(new Role(Roles.ROLE_VETERINARIAN)))
                .isInstanceOf(UnsupportedOperationException.class);
        user.addRole(new Role(Roles.ROLE_VETERINARIAN));
        assertThat(user.hasRole(new Role(Roles.ROLE_VETERINARIAN))).isTrue();
    }

    @Test
    void reconstituteDoesNotRecordEventsAndValidatesState() {
        var user = User.reconstitute(
                7L,
                new EmailAddress("vet@vantara.pe"),
                new PasswordHash("hash"),
                List.of(new Role(Roles.ROLE_VETERINARIAN)),
                AccountStatus.ACTIVE,
                null);

        assertThat(user.getId()).isEqualTo(7L);
        assertThat(user.domainEvents()).isEmpty();
        assertThatThrownBy(() -> User.reconstitute(
                0L, new EmailAddress("vet@vantara.pe"), new PasswordHash("hash"),
                List.of(new Role(Roles.ROLE_VETERINARIAN)), AccountStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> User.reconstitute(
                7L, new EmailAddress("vet@vantara.pe"), new PasswordHash("hash"),
                List.of(), AccountStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void clearDomainEventsRemovesPendingEvents() {
        var user = newUser();

        assertThat(user.domainEvents()).hasSize(1);
        user.clearDomainEvents();
        assertThat(user.domainEvents()).isEmpty();
    }
}

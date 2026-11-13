package com.vantara.platform.iam.infrastructure.persistence.jpa;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.aggregates.VerificationCode;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.valueobjects.AccountStatus;
import com.vantara.platform.iam.domain.model.valueobjects.ContactMessage;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationOutcome;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import com.vantara.platform.iam.domain.repositories.ContactRequestRepository;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.iam.domain.repositories.VerificationCodeRepository;
import com.vantara.platform.iam.infrastructure.persistence.jpa.adapters.ContactRequestRepositoryImpl;
import com.vantara.platform.iam.infrastructure.persistence.jpa.adapters.RoleRepositoryImpl;
import com.vantara.platform.iam.infrastructure.persistence.jpa.adapters.UserRepositoryImpl;
import com.vantara.platform.iam.infrastructure.persistence.jpa.adapters.VerificationCodeRepositoryImpl;
import com.vantara.platform.shared.infrastructure.events.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({
        RoleRepositoryImpl.class,
        UserRepositoryImpl.class,
        VerificationCodeRepositoryImpl.class,
        ContactRequestRepositoryImpl.class,
        DomainEventPublisher.class
})
class IamPersistenceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private ContactRequestRepository contactRequestRepository;

    private Role rancher;

    @BeforeEach
    void seedRole() {
        rancher = roleRepository.findByName(Roles.ROLE_RANCHER)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_RANCHER)));
    }

    private User registerUser(String email) {
        return userRepository.save(User.register(
                new EmailAddress(email), new PasswordHash("$2a$hash"), rancher, NOW));
    }

    @Test
    void savesAndReadsRoles() {
        assertNotNull(rancher.getId());
        assertTrue(roleRepository.existsByName(Roles.ROLE_RANCHER));
        assertFalse(roleRepository.existsByName(Roles.ROLE_VETERINARIAN));
        assertEquals(1, roleRepository.findAll().size());
    }

    @Test
    void savesAndReadsUserWithRolesAndId() {
        var saved = registerUser("Rancher@Example.com");

        assertNotNull(saved.getId());
        var found = userRepository.findByEmail(new EmailAddress("rancher@example.com")).orElseThrow();
        assertEquals(saved.getId(), found.getId());
        assertEquals(AccountStatus.PENDING_VERIFICATION, found.getStatus());
        assertTrue(found.hasRole(rancher));
        assertEquals(userRepository.findById(saved.getId()).orElseThrow().getEmail(), found.getEmail());
        assertTrue(userRepository.existsByEmail(new EmailAddress("rancher@example.com")));
    }

    @Test
    void updatesUserState() {
        var saved = registerUser("update@example.com");
        saved.verifyEmail(NOW.plusSeconds(60));
        saved.recordSignIn(NOW.plusSeconds(120));
        userRepository.save(saved);

        var found = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals(AccountStatus.ACTIVE, found.getStatus());
        assertEquals(NOW.plusSeconds(120), found.getLastSignInAt());
    }

    @Test
    void publishedEventsAreClearedAfterSaving() {
        var user = User.register(new EmailAddress("events@example.com"), new PasswordHash("$2a$hash"), rancher, NOW);
        assertEquals(1, user.domainEvents().size());

        userRepository.save(user);

        assertTrue(user.domainEvents().isEmpty());
    }

    @Test
    void emailMustBeUnique() {
        registerUser("dup@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            registerUser("dup@example.com");
            userRepository.findAll();
        });
    }

    @Test
    void savesVerificationCodeAndKeepsFailedAttempts() {
        var user = registerUser("code@example.com");
        var userId = new UserId(user.getId());
        var code = VerificationCode.issue(
                userId, new VerificationCodeValue("123456"), VerificationPurpose.EMAIL_VERIFICATION,
                NOW, Duration.ofMinutes(15));
        var saved = verificationCodeRepository.save(code);
        assertNotNull(saved.getId());

        assertEquals(VerificationOutcome.INVALID, saved.verify(new VerificationCodeValue("000000"), NOW));
        verificationCodeRepository.save(saved);

        var found = verificationCodeRepository
                .findLatestByUserIdAndPurpose(userId, VerificationPurpose.EMAIL_VERIFICATION).orElseThrow();
        assertEquals(1, found.getFailedAttempts());
        assertEquals(saved.getExpiresAt(), found.getExpiresAt());
        assertFalse(found.isConsumed());
    }

    @Test
    void latestVerificationCodeIsTheMostRecentOne() {
        var user = registerUser("latest@example.com");
        var userId = new UserId(user.getId());
        verificationCodeRepository.save(VerificationCode.issue(
                userId, new VerificationCodeValue("111111"), VerificationPurpose.PASSWORD_RECOVERY,
                NOW, Duration.ofMinutes(15)));
        verificationCodeRepository.save(VerificationCode.issue(
                userId, new VerificationCodeValue("222222"), VerificationPurpose.PASSWORD_RECOVERY,
                NOW.plusSeconds(30), Duration.ofMinutes(15)));

        var latest = verificationCodeRepository
                .findLatestByUserIdAndPurpose(userId, VerificationPurpose.PASSWORD_RECOVERY).orElseThrow();

        assertEquals("222222", latest.getCode().value());
        assertTrue(verificationCodeRepository
                .findLatestByUserIdAndPurpose(userId, VerificationPurpose.EMAIL_VERIFICATION).isEmpty());
    }

    @Test
    void savesAndReadsContactRequest() {
        var request = ContactRequest.send(
                new FullName("Ana Perez"), new EmailAddress("ana@example.com"),
                new ContactMessage("I want to know more about Vantara"), NOW);

        var saved = contactRequestRepository.save(request);

        assertNotNull(saved.getId());
        assertEquals("Ana Perez", saved.getFullName().value());
        assertEquals(NOW, saved.getSentAt());
        assertTrue(request.domainEvents().isEmpty());
    }
}

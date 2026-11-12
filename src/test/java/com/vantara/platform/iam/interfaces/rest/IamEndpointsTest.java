package com.vantara.platform.iam.interfaces.rest;

import com.vantara.platform.iam.application.internal.outboundservices.email.EmailService;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the IAM REST API end to end with real security, services, persistence (H2) and the
 * database-backed roles seeded at startup. Emails are captured instead of sent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IamEndpointsTest {

    private static final String PASSWORD = "Password1";
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @TestConfiguration
    static class CapturingEmailConfiguration {
        @Bean
        @Primary
        CapturingEmailService capturingEmailService() {
            return new CapturingEmailService();
        }
    }

    static class CapturingEmailService implements EmailService {
        final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
        final Map<String, String> recoveryCodes = new ConcurrentHashMap<>();
        final Map<String, Integer> contactConfirmations = new ConcurrentHashMap<>();

        @Override
        public void sendEmailVerificationCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt) {
            verificationCodes.put(to.address(), code.value());
        }

        @Override
        public void sendPasswordRecoveryCode(EmailAddress to, VerificationCodeValue code, Instant expiresAt) {
            recoveryCodes.put(to.address(), code.value());
        }

        @Override
        public void sendContactConfirmation(EmailAddress to, FullName name) {
            contactConfirmations.merge(to.address(), 1, Integer::sum);
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CapturingEmailService emails;

    private static String newEmail() {
        return "user" + COUNTER.incrementAndGet() + "@example.com";
    }

    private ResultActions postJson(String path, String json) throws Exception {
        return mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions signUp(String email) throws Exception {
        return postJson("/api/v1/authentication/sign-up",
                "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, PASSWORD));
    }

    private ResultActions signIn(String email, String password) throws Exception {
        return postJson("/api/v1/authentication/sign-in",
                "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password));
    }

    private void verify(String email) throws Exception {
        postJson("/api/v1/authentication/verify-email",
                "{\"email\":\"%s\",\"code\":\"%s\"}".formatted(email, emails.verificationCodes.get(email)))
                .andExpect(status().isOk());
    }

    private String tokenOf(String email, String password) throws Exception {
        var body = signIn(email, password).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var marker = "\"token\":\"";
        var start = body.indexOf(marker) + marker.length();
        var token = body.substring(start, body.indexOf('"', start));
        assertNotNull(token);
        return token;
    }

    private static MockHttpServletRequestBuilder bearer(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }

    @Test
    void registrationVerificationAndSignInFlow() throws Exception {
        var email = newEmail();

        signUp(email).andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_RANCHER"))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("hash"))));

        // An unverified account cannot sign in.
        signIn(email, PASSWORD).andExpect(status().isUnprocessableContent());

        // A wrong code is rejected, the right one activates the account.
        postJson("/api/v1/authentication/verify-email",
                "{\"email\":\"%s\",\"code\":\"%s\"}".formatted(email, wrongCode(emails.verificationCodes.get(email))))
                .andExpect(status().isBadRequest());
        verify(email);

        var token = tokenOf(email, PASSWORD);
        mockMvc.perform(bearer(get("/api/v1/users/me"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    private static String wrongCode(String code) {
        return code.equals("000000") ? "000001" : "000000";
    }

    @Test
    void signUpRejectsInvalidDataAndDuplicates() throws Exception {
        var email = newEmail();

        postJson("/api/v1/authentication/sign-up", "{\"email\":\"not-an-email\",\"password\":\"Password1\"}")
                .andExpect(status().isBadRequest());
        postJson("/api/v1/authentication/sign-up", "{\"email\":\"%s\",\"password\":\"short\"}".formatted(email))
                .andExpect(status().isBadRequest());
        postJson("/api/v1/authentication/sign-up", "{}").andExpect(status().isBadRequest());
        postJson("/api/v1/authentication/sign-up", "not json").andExpect(status().isBadRequest());

        signUp(email).andExpect(status().isCreated());
        signUp(email).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("USER_CONFLICT"));
    }

    @Test
    void signInDoesNotRevealWhetherTheEmailExists() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());
        verify(email);

        var wrongPassword = signIn(email, "Wrong12345").andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        var unknownEmail = signIn("ghost-" + email, PASSWORD).andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(wrongPassword, unknownEmail);
    }

    @Test
    void errorMessagesFollowTheAcceptLanguageHeader() throws Exception {
        mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("{\"email\":\"nobody@example.com\",\"password\":\"Password1\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Correo electrónico o contraseña inválidos"));
    }

    @Test
    void protectedEndpointsRequireAValidToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        mockMvc.perform(bearer(get("/api/v1/users/me"), "garbage")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/roles")).andExpect(status().isUnauthorized());
    }

    @Test
    void rolesAreSeededAndListedForAuthenticatedUsers() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());
        verify(email);

        mockMvc.perform(bearer(get("/api/v1/roles"), tokenOf(email, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void aUserCannotReadAnotherAccountById() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());
        verify(email);
        var token = tokenOf(email, PASSWORD);
        var ownId = mockMvc.perform(bearer(get("/api/v1/users/me"), token)).andReturn().getResponse().getContentAsString();
        var id = ownId.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(bearer(get("/api/v1/users/" + id), token)).andExpect(status().isOk());
        mockMvc.perform(bearer(get("/api/v1/users/" + (Long.parseLong(id) + 1000)), token))
                .andExpect(status().isNotFound());
    }

    @Test
    void passwordRecoveryFlow() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());
        verify(email);

        // Same answer for an unknown email.
        postJson("/api/v1/authentication/request-password-recovery", "{\"email\":\"ghost-%s\"}".formatted(email))
                .andExpect(status().isAccepted());
        postJson("/api/v1/authentication/request-password-recovery", "{\"email\":\"%s\"}".formatted(email))
                .andExpect(status().isAccepted());

        // A weak password is rejected without consuming the code.
        postJson("/api/v1/authentication/recover-password",
                "{\"email\":\"%s\",\"code\":\"%s\",\"newPassword\":\"weak\"}"
                        .formatted(email, emails.recoveryCodes.get(email)))
                .andExpect(status().isBadRequest());
        postJson("/api/v1/authentication/recover-password",
                "{\"email\":\"%s\",\"code\":\"%s\",\"newPassword\":\"NewPassword2\"}"
                        .formatted(email, emails.recoveryCodes.get(email)))
                .andExpect(status().isOk());

        signIn(email, PASSWORD).andExpect(status().isUnauthorized());
        signIn(email, "NewPassword2").andExpect(status().isOk());
    }

    @Test
    void verificationCodeIsBlockedAfterTooManyWrongAttempts() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());
        var wrong = wrongCode(emails.verificationCodes.get(email));
        var body = "{\"email\":\"%s\",\"code\":\"%s\"}".formatted(email, wrong);

        for (int attempt = 0; attempt < 5; attempt++) {
            postJson("/api/v1/authentication/verify-email", body).andExpect(status().isBadRequest());
        }
        // Even the right code is refused now.
        postJson("/api/v1/authentication/verify-email",
                "{\"email\":\"%s\",\"code\":\"%s\"}".formatted(email, emails.verificationCodes.get(email)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void resendingTheVerificationCodeIsRateLimited() throws Exception {
        var email = newEmail();
        signUp(email).andExpect(status().isCreated());

        postJson("/api/v1/authentication/send-verification-code", "{\"email\":\"%s\"}".formatted(email))
                .andExpect(status().isUnprocessableContent());
        postJson("/api/v1/authentication/send-verification-code", "{\"email\":\"ghost-%s\"}".formatted(email))
                .andExpect(status().isAccepted());
    }

    @Test
    void contactFormIsPublicAndValidated() throws Exception {
        var email = newEmail();

        postJson("/api/v1/contact-requests",
                "{\"fullName\":\"Ana Perez\",\"email\":\"%s\",\"message\":\"I want to know more about Vantara\"}"
                        .formatted(email))
                .andExpect(status().isCreated());
        org.junit.jupiter.api.Assertions.assertEquals(1, emails.contactConfirmations.get(email));

        postJson("/api/v1/contact-requests",
                "{\"fullName\":\"Ana Perez\",\"email\":\"%s\",\"message\":\"short\"}".formatted(email))
                .andExpect(status().isBadRequest());
        postJson("/api/v1/contact-requests", "{}").andExpect(status().isBadRequest());
    }
}

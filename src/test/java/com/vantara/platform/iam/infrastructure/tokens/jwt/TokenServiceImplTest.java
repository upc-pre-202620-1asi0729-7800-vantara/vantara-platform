package com.vantara.platform.iam.infrastructure.tokens.jwt;

import com.vantara.platform.iam.infrastructure.tokens.jwt.services.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceImplTest {

    private static final String SECRET = "test-secret-test-secret-test-secret-123456";
    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private TokenServiceImpl serviceAt(Instant instant, String secret) {
        return new TokenServiceImpl(secret, 1, Clock.fixed(instant, ZoneOffset.UTC));
    }

    @Test
    void generatedTokenIsValidAndCarriesTheEmail() {
        var service = serviceAt(NOW, SECRET);

        var token = service.generateToken("rancher@example.com");

        assertTrue(service.validateToken(token));
        assertEquals("rancher@example.com", service.getEmailFromToken(token));
    }

    @Test
    void expiredTokenIsRejected() {
        var token = serviceAt(NOW, SECRET).generateToken("rancher@example.com");

        assertFalse(serviceAt(NOW.plus(Duration.ofDays(2)), SECRET).validateToken(token));
    }

    @Test
    void tokenSignedWithAnotherSecretOrGarbageIsRejected() {
        var token = serviceAt(NOW, "another-secret-another-secret-another-1234").generateToken("a@example.com");
        var service = serviceAt(NOW, SECRET);

        assertFalse(service.validateToken(token));
        assertFalse(service.validateToken("not-a-token"));
        assertFalse(service.validateToken(""));
    }

    @Test
    void shortSecretIsRejectedAtStartup() {
        assertThrows(IllegalStateException.class, () -> serviceAt(NOW, "too-short"));
    }

    @Test
    void readsTheBearerTokenFromTheAuthorizationHeader() {
        var service = serviceAt(NOW, SECRET);
        var request = new MockHttpServletRequest();
        assertNull(service.getBearerTokenFrom(request));

        request.addHeader("Authorization", "Basic abc");
        assertNull(service.getBearerTokenFrom(request));

        var bearer = new MockHttpServletRequest();
        bearer.addHeader("Authorization", "Bearer abc.def");
        assertEquals("abc.def", service.getBearerTokenFrom(bearer));
    }
}

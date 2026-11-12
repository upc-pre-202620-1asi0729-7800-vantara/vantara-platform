package com.vantara.platform.iam.infrastructure.tokens.jwt.services;

import com.vantara.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;

/**
 * JWT implementation of the token port. The signing secret comes from {@code authorization.jwt.secret}
 * (environment variable {@code JWT_SECRET}) and must have at least 32 bytes.
 */
@Service
public class TokenServiceImpl implements BearerTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final Duration expiration;
    private final Clock clock;

    public TokenServiceImpl(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days}") int expirationDays,
            Clock clock) {
        var keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("authorization.jwt.secret must have at least 32 characters");
        }
        if (expirationDays < 1) {
            throw new IllegalStateException("authorization.jwt.expiration.days must be at least 1");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = Duration.ofDays(expirationDays);
        this.clock = clock;
    }

    @Override
    public String generateToken(String email) {
        var issuedAt = clock.instant();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Only the kind of failure is logged; tokens and claims are never written to logs.
            LOGGER.debug("Token rejected: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX) && header.length() > BEARER_PREFIX.length()) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

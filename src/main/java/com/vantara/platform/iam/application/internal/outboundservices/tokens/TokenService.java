package com.vantara.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound port for bearer token issuance and validation.
 */
public interface TokenService {

    /**
     * @param userId id of the authenticated user, used as token subject (it never changes, unlike the email)
     * @return the signed token
     */
    String generateToken(Long userId);

    /**
     * @return {@code true} when the token is well formed, correctly signed and not expired
     */
    boolean validateToken(String token);

    /**
     * @return the user id stored in a valid token
     * @throws IllegalArgumentException when the token subject is not a user id
     */
    Long getUserIdFromToken(String token);
}

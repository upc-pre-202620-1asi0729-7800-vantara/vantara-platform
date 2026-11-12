package com.vantara.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound port for bearer token issuance and validation.
 */
public interface TokenService {

    /**
     * @param email email address of the authenticated user, used as token subject
     * @return the signed token
     */
    String generateToken(String email);

    /**
     * @return {@code true} when the token is well formed, correctly signed and not expired
     */
    boolean validateToken(String token);

    /**
     * @return the email address stored in a valid token
     */
    String getEmailFromToken(String token);
}

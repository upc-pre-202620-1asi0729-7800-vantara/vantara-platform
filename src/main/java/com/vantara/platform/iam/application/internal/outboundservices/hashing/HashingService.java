package com.vantara.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound port for password hashing. The domain only ever sees the resulting {@code PasswordHash}.
 */
public interface HashingService {

    /**
     * @param rawPassword plain-text password
     * @return the encoded (hashed) password
     */
    String encode(CharSequence rawPassword);

    /**
     * @return {@code true} when the raw password matches the encoded one
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}

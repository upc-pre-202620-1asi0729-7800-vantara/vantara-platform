package com.vantara.platform.iam.infrastructure.tokens.jwt;

import com.vantara.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Token service that can also read the bearer token from an HTTP request.
 */
public interface BearerTokenService extends TokenService {

    /**
     * @return the token of the {@code Authorization: Bearer} header, or {@code null} when absent
     */
    String getBearerTokenFrom(HttpServletRequest request);
}

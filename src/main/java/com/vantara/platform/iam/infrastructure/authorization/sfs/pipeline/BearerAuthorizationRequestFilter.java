package com.vantara.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.vantara.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.vantara.platform.iam.infrastructure.authorization.sfs.services.UserDetailsByIdService;
import com.vantara.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates a request when it carries a valid bearer token. Invalid or missing tokens leave the
 * request anonymous, so the security rules answer with 401 where authentication is required.
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);

    private final BearerTokenService tokenService;
    private final UserDetailsByIdService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsByIdService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = tokenService.getBearerTokenFrom(request);
            if (token != null && tokenService.validateToken(token)) {
                var userDetails = userDetailsService.loadUserById(tokenService.getUserIdFromToken(token));
                if (userDetails.isEnabled()) {
                    SecurityContextHolder.getContext().setAuthentication(
                            UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
                }
            }
        } catch (RuntimeException e) {
            LOGGER.debug("Bearer authentication skipped: {}", e.getClass().getSimpleName());
        }
        filterChain.doFilter(request, response);
    }
}

package com.vantara.platform.iam.infrastructure.authorization.sfs.configuration;

import com.vantara.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.vantara.platform.iam.infrastructure.authorization.sfs.services.UserDetailsByIdService;
import com.vantara.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.vantara.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Stateless security: bearer tokens, no sessions, no CSRF. Sign-up, sign-in, email verification,
 * password recovery, the contact form and Swagger are public; everything else needs a valid token.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final UserDetailsByIdService userDetailsByIdService;
    private final BearerTokenService tokenService;
    private final BCryptHashingService hashingService;
    private final AuthenticationEntryPoint unauthorizedRequestHandler;
    private final List<String> allowedOrigins;

    public WebSecurityConfiguration(
            @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService,
            UserDetailsByIdService userDetailsByIdService,
            BearerTokenService tokenService,
            BCryptHashingService hashingService,
            AuthenticationEntryPoint authenticationEntryPoint,
            @Value("${authorization.cors.allowed-origins}") List<String> allowedOrigins) {
        this.userDetailsService = userDetailsService;
        this.userDetailsByIdService = userDetailsByIdService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.unauthorizedRequestHandler = authenticationEntryPoint;
        if (allowedOrigins.stream().anyMatch(origin -> origin.isBlank() || origin.contains("*"))) {
            throw new IllegalStateException(
                    "authorization.cors.allowed-origins must list explicit origins; wildcards are not allowed");
        }
        this.allowedOrigins = List.copyOf(allowedOrigins);
    }

    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsByIdService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(hashingService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return hashingService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
            var configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(allowedOrigins);
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Accept-Language"));
            configuration.setMaxAge(3600L);
            return configuration;
        }));
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/api/v1/authentication/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contact-requests").permitAll()
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

package com.vantara.platform.shared.infrastructure.configuration.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures cross-origin access to the platform REST API.
 *
 * <p>The allowed origins are configurable through
 * {@code vantara.cors.allowed-origins}. The default value targets the Angular
 * development server.</p>
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfiguration(
            @Value("${vantara.cors.allowed-origins:http://localhost:4200}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins.split("\\s*,\\s*");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

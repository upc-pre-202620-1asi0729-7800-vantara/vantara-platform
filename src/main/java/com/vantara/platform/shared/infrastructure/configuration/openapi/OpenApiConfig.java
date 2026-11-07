package com.vantara.platform.shared.infrastructure.configuration.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central OpenAPI metadata shared by all bounded contexts.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vantaraPlatformOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vantara Platform API")
                        .version("0.1.0")
                        .description("""
                                REST API documentation for the Vantara / Hatarium platform.
                                Organized by bounded contexts following Domain-Driven Design.
                                """)
                        .contact(new Contact().name("Vantara Platform Team")));
    }
}

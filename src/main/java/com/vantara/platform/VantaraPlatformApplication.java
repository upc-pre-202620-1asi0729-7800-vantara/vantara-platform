package com.vantara.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Vantara Platform Spring Boot application.
 *
 * <p>Enables JPA auditing so bounded-context entities can use auditing
 * annotations when they are added.</p>
 */
@EnableJpaAuditing
@SpringBootApplication
public class VantaraPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(VantaraPlatformApplication.class, args);
    }
}

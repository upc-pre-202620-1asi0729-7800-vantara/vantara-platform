package com.vantara.platform.shared.infrastructure.time.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Provides a testable clock shared by all bounded contexts.
 */
@Configuration
public class TimeConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}

package com.vantara.platform.iam.infrastructure.hashing.bcrypt;

import com.vantara.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Marker interface of the BCrypt hashing service. It is both the application port and the Spring
 * Security {@link PasswordEncoder}, so a single bean is used for hashing and authentication.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}

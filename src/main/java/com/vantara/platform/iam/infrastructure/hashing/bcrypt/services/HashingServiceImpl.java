package com.vantara.platform.iam.infrastructure.hashing.bcrypt.services;

import com.vantara.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt implementation of the hashing port. BCrypt only reads the first 72 bytes of the password,
 * which is why {@code RawPassword} limits the length in bytes.
 */
@Service
public class HashingServiceImpl implements BCryptHashingService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

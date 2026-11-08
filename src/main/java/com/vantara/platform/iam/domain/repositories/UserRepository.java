package com.vantara.platform.iam.domain.repositories;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for {@link User} aggregates.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);

    List<User> findAll();
}

package com.vantara.platform.iam.application.internal.queryservices;

import com.vantara.platform.iam.application.queryservices.UserQueryService;
import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.vantara.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.vantara.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    /** An email with an invalid format cannot belong to any account, so the result is empty. */
    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        try {
            return userRepository.findByEmail(new EmailAddress(query.email()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}


package com.vantara.platform.iam.interfaces.acl;

import com.vantara.platform.iam.application.commandservices.UserCommandService;
import com.vantara.platform.iam.application.queryservices.UserQueryService;
import com.vantara.platform.iam.domain.model.commands.SignUpCommand;
import com.vantara.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.vantara.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Anti-corruption facade that exposes the IAM capabilities other bounded contexts may use.
 * It only handles primitive values, so no IAM domain class leaks out of the context.
 */
@Component
public class IamContextFacade {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public IamContextFacade(
            UserCommandService userCommandService,
            UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    /**
     * Registers a user. The account stays pending until the email address is verified.
     *
     * @param email email address of the new user
     * @param password plain-text password
     * @param roleName role name, or null for the default role
     * @return the id of the new user, or empty when the registration failed
     */
    public Optional<Long> createUser(String email, String password, String roleName) {
        var result = userCommandService.handle(
                new SignUpCommand(email, password, roleName));

        if (result instanceof Result.Success<?, ?> success
                && success.value() instanceof com.vantara.platform.iam.domain.model.aggregates.User user) {
            return Optional.of(user.getId());
        }

        return Optional.empty();
    }

    /**
     * @return the id of the user with that email address, or empty when there is none
     */
    public Optional<Long> fetchUserIdByEmail(String email) {
        return userQueryService.handle(new GetUserByEmailQuery(email))
                .map(user -> user.getId());
    }

    /**
     * @return the email address of the user with that id, or empty when there is none
     */
    public Optional<String> fetchEmailByUserId(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getEmail().address());
    }

    /**
     * Returns the role names assigned to the user with the given email.
     *
     * @param email authenticated user's email
     * @return assigned role names, or empty when the user does not exist
     */
    public Optional<Set<String>> fetchRoleNamesByEmail(String email) {
        return userQueryService.handle(new GetUserByEmailQuery(email))
                .map(user -> user.getRoles().stream()
                        .map(role -> role.getStringName())
                        .collect(Collectors.toUnmodifiableSet()));

    }

    /**
     * Returns the role names assigned to the user with the given IAM ID.
     *
     * @param userId IAM user identifier
     * @return assigned role names, or empty when the user does not exist
     */
    public Optional<Set<String>> fetchRoleNamesByUserId(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getRoles().stream()
                        .map(role -> role.getStringName())
                        .collect(Collectors.toUnmodifiableSet()));
    }
}

package com.vantara.platform.iam.application.queryservices;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.vantara.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.vantara.platform.iam.domain.model.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Read-side contract of the user queries.
 */
public interface UserQueryService {

    List<User> handle(GetAllUsersQuery query);

    Optional<User> handle(GetUserByIdQuery query);

    Optional<User> handle(GetUserByEmailQuery query);
}

package com.vantara.platform.iam.infrastructure.authorization.sfs.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads the Spring Security view of an account from its id, which is the subject of the bearer token.
 */
public interface UserDetailsByIdService {

    /**
     * @param userId id of the account
     * @return the account details
     * @throws UsernameNotFoundException when there is no account with that id
     */
    UserDetails loadUserById(Long userId) throws UsernameNotFoundException;
}

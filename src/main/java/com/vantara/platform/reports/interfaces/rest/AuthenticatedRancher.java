package com.vantara.platform.reports.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

/**
 * Resolves the rancher of a request from the authenticated principal through the IAM facade. The client never
 * sends its own identity: the rancher is always the account of the bearer token.
 */
@Component
public class AuthenticatedRancher {

    private final IamContextFacade iamContextFacade;

    public AuthenticatedRancher(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * @return the owner key used by livestock, or empty when the principal has no IAM account
     */
    public Optional<String> resolve(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return Optional.empty();
        }
        return iamContextFacade.fetchUserIdByEmail(principal.getName()).map(String::valueOf);
    }
}

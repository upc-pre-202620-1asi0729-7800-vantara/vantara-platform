
package com.vantara.platform.iam.interfaces.acl;

import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves the canonical IAM identity and roles from the authenticated principal.
 */
@Component
public class AuthenticatedUserIdentity {

    private final IamContextFacade iam;

    public AuthenticatedUserIdentity(IamContextFacade iam) {
        this.iam = iam;
    }

    /**
     * Resolves the IAM user ID from the authenticated principal.
     */
    public Optional<String> userId(Principal principal) {
        if (principal == null
                || principal.getName() == null
                || principal.getName().isBlank()) {
            return Optional.empty();
        }

        return iam.fetchUserIdByEmail(principal.getName())
                .map(String::valueOf);
    }

    /**
     * Resolves the rancher ID.
     * The rancher ID is the IAM user ID, not a profile ID.
     */
    public Optional<String> rancherId(Principal principal) {
        return userId(principal);
    }

    /**
     * Resolves the roles assigned to the authenticated user.
     */
    public Optional<Set<String>> roleNames(Principal principal) {
        if (principal == null
                || principal.getName() == null
                || principal.getName().isBlank()) {
            return Optional.empty();
        }

        return iam.fetchRoleNamesByEmail(principal.getName());
    }

    /**
     * Checks whether the authenticated user has the specified role.
     */
    public boolean hasRole(Principal principal, String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return false;
        }

        return roleNames(principal)
                .map(roles -> roles.contains(roleName))
                .orElse(false);
    }

    public boolean isRancher(Principal principal) {
        return hasRole(principal, "ROLE_RANCHER");
    }

    public boolean isVeterinarian(Principal principal) {
        return hasRole(principal, "ROLE_VETERINARIAN");
    }
}

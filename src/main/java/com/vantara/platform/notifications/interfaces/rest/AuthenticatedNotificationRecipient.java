package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Component
public class AuthenticatedNotificationRecipient {
    private final ObjectProvider<UserRepository> users;

    public AuthenticatedNotificationRecipient(ObjectProvider<UserRepository> users) {
        this.users = users;
    }

    public RecipientId resolve(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var repository = users.getIfAvailable();
        if (repository == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "IAM is unavailable");
        }
        EmailAddress email;
        try {
            email = new EmailAddress(principal.getName());
        } catch (IllegalArgumentException invalidIdentity) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.findByEmail(email)
                .map(user -> new RecipientId(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}

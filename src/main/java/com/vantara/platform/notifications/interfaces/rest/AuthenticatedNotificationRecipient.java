package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.notifications.application.queryservices.NotificationRecipientLookup;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Component
public class AuthenticatedNotificationRecipient {
    private final ObjectProvider<NotificationRecipientLookup> recipients;

    public AuthenticatedNotificationRecipient(ObjectProvider<NotificationRecipientLookup> recipients) {
        this.recipients = recipients;
    }

    public RecipientId resolve(Principal principal) {
        String name = principal == null ? null : principal.getName();
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var lookup = recipients.getIfAvailable();
        if (lookup == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "IAM is unavailable");
        }
        return lookup.findByAuthenticatedName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}

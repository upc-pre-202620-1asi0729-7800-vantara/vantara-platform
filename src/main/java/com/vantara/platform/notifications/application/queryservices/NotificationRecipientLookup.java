package com.vantara.platform.notifications.application.queryservices;

import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;

import java.util.Optional;

/** Resolves the name supplied by the server's authenticated principal to an IAM account ID. */
public interface NotificationRecipientLookup {
    Optional<RecipientId> findByAuthenticatedName(String name);
}

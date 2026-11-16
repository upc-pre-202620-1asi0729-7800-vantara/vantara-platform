package com.vantara.platform.notifications.infrastructure.iam;

import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.notifications.application.queryservices.NotificationRecipientLookup;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import org.springframework.stereotype.Component;

@Component
public class IamNotificationRecipientLookup implements NotificationRecipientLookup {

    private final UserRepository userRepository;

    public IamNotificationRecipientLookup(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public java.util.Optional<RecipientId> findByAuthenticatedName(String name) {
        return userRepository.findByEmail(new EmailAddress(name))
                .map(user -> new RecipientId(user.getId()));
    }
}